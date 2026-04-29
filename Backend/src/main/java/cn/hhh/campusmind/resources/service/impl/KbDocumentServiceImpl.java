package cn.hhh.campusmind.resources.service.impl;

import cn.hhh.campusmind.common.constants.RabbitMqConstants;
import cn.hhh.campusmind.common.page.PageResult;
import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.common.utils.AliOssUtil;
import cn.hhh.campusmind.common.utils.UserContext;
import cn.hhh.campusmind.course.service.ICourseService;
import cn.hhh.campusmind.department.service.ICollegeService;
import cn.hhh.campusmind.resources.domain.dto.DocumentPageQuery;
import cn.hhh.campusmind.resources.domain.dto.UploadDocumentDTO;
import cn.hhh.campusmind.resources.domain.enums.*;
import cn.hhh.campusmind.resources.domain.messages.DocumentForChunkMessage;
import cn.hhh.campusmind.resources.domain.po.KbChunk;
import cn.hhh.campusmind.resources.domain.po.KbChunkEmbedding;
import cn.hhh.campusmind.resources.domain.po.KbDocument;
import cn.hhh.campusmind.resources.domain.po.KbIndexTask;
import cn.hhh.campusmind.resources.domain.vo.DocumentVO;
import cn.hhh.campusmind.resources.mapper.KbChunkEmbeddingMapper;
import cn.hhh.campusmind.resources.mapper.KbChunkMapper;
import cn.hhh.campusmind.resources.mapper.KbDocumentMapper;
import cn.hhh.campusmind.resources.service.IKbDocumentService;
import cn.hhh.campusmind.resources.service.IKbIndexTaskService;
import cn.hhh.campusmind.user.domain.enums.UserType;
import cn.hhh.campusmind.user.domain.po.SysUser;
import cn.hhh.campusmind.user.service.ISysUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.util.*;


/**
 * <p>
 * 知识库文档主表（老师上传文件后落库） 服务实现类
 * </p>
 *
 * @author thyme
 * @since 2026-04-27
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KbDocumentServiceImpl extends ServiceImpl<KbDocumentMapper, KbDocument> implements IKbDocumentService {

    private final ISysUserService sysUserService;
    private final RabbitTemplate rabbitTemplate;
    private final IKbIndexTaskService indexTaskService;
    private final KbChunkMapper kbChunkMapper;
    private final KbChunkEmbeddingMapper kbChunkEmbeddingMapper;
    private final ICollegeService collegeService;
    private final AliOssUtil aliOssUtil;
    private final ICourseService courseService;

    private static final int OSS_EXPIRE = 2 * 3600;


    @Override
    public Result<Void> uploadDocument(UploadDocumentDTO uploadDocumentDTO) {
        // 判断是否是教师身份
        Long userId = UserContext.getUserId();
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        if (user.getRole() != UserType.TEACHER && user.getRole() != UserType.ADMIN) {
            return Result.fail("用户不是教师或管理员身份");
        }

        // 从文件名推断后缀；若推断失败，降级为 bin
        String fileName = uploadDocumentDTO.getFileName().trim();
        String objectKey = uploadDocumentDTO.getObjectKey().trim();
        int dotIndex = fileName.lastIndexOf('.');
        FileExtEnum fileExt = FileExtEnum.valueOf(fileName.substring(dotIndex + 1).toUpperCase(Locale.ROOT));

        // 根据文件类型粗分主模态；后续可在解析服务中二次纠正
        String fileType = uploadDocumentDTO.getFileType().trim();
        String lowerFileType = fileType.toLowerCase(Locale.ROOT);
        DocumentTypeEnum modality;
        if (lowerFileType.startsWith("image/")) {
            modality = DocumentTypeEnum.IMAGE;
        } else if (lowerFileType.startsWith("audio/")) {
            modality = DocumentTypeEnum.AUDIO;
        } else if (lowerFileType.startsWith("video/")) {
            modality = DocumentTypeEnum.VIDEO;
        } else if (lowerFileType.startsWith("text/")) {
            modality = DocumentTypeEnum.TEXT;
        } else {
            modality = DocumentTypeEnum.MIXED;
        }

        // 文件去重：优先 SHA-256
        Optional<KbDocument> duplicate = findExistingDuplicateInCourse(
                uploadDocumentDTO.getCourseId(),
                uploadDocumentDTO.getFileSha256(),
                objectKey
        );
        if (duplicate.isPresent()) {
            return Result.fail("该课程下已存在相同文件，请勿重复上传");
        }

        // 保存文档元信息（原始文件在 OSS，通过 objectKey 关联）
        KbDocument kbDocument = new KbDocument();
        kbDocument.setDocNo("DOC-" + UUID.randomUUID().toString().replace("-", ""));
        kbDocument.setTeacherId(userId);
        kbDocument.setCourseId(uploadDocumentDTO.getCourseId());
        // TODO
        kbDocument.setTitle(fileName);
        kbDocument.setOriginalFilename(fileName);
        kbDocument.setStorageKey(objectKey);
        kbDocument.setFileExt(fileExt);
        kbDocument.setMimeType(resolveMimeType(fileType, fileExt.getCode()));
        kbDocument.setFileSizeBytes(uploadDocumentDTO.getFileSize());
        kbDocument.setSourceType(SourceTypeEnum.UPLOAD);
        kbDocument.setModality(modality);
        kbDocument.setFileSha256(uploadDocumentDTO.getFileSha256());
        // TODO 目前只支持中文文件，后续可根据需要扩展其他语言
        kbDocument.setLanguage("Chinese");
        kbDocument.setChunkCount(0);
        kbDocument.setVectorCount(0);
        kbDocument.setVersionNo(1);
        kbDocument.setIsDeleted(0);
        save(kbDocument);

        // 生成索引任务
        KbIndexTask indexTask = new KbIndexTask();
        indexTask.setTaskNo("TASK-" + UUID.randomUUID().toString().replace("-", ""));
        indexTask.setDocId(kbDocument.getId());
        indexTask.setTeacherId(userId);
        indexTask.setCourseId(uploadDocumentDTO.getCourseId());
        indexTask.setTaskType(TaskTypeEnum.DOCUMENT_INDEX);
        indexTask.setStatus(TaskStatusEnum.CREATED);
        indexTask.setCurrentStep(TaskStepEnum.INIT);
        indexTask.setRetryCount(0);
        indexTask.setTraceId(uploadDocumentDTO.getTraceId());
        indexTask.setCostMs(0L);
        indexTask.setResultJson("{}");
        indexTask.setErrorCode("");
        indexTask.setErrorMessage("");
        indexTask.setIsDeleted(0);
        indexTaskService.save(indexTask);


        // 将 kbDocument.id/docNo/objectKey 投递到 MQ，触发 Python 解析、切分、向量化任务。
        rabbitTemplate.convertAndSend(
                RabbitMqConstants.KB_DOCUMENT_PARSE_EXCHANGE,
                RabbitMqConstants.KB_DOCUMENT_PARSE_ROUTING_KEY,
                DocumentForChunkMessage.builder()
                        .docNo(kbDocument.getDocNo())
                        .documentId(kbDocument.getId())
                        .fileSizeBytes(kbDocument.getFileSizeBytes())
                        .storageKey(kbDocument.getStorageKey())
                        .mimeType(kbDocument.getMimeType())
                        .originalFilename(kbDocument.getOriginalFilename())
                        .teacherId(userId)
                        .traceId(uploadDocumentDTO.getTraceId())
                        .courseId(uploadDocumentDTO.getCourseId())
                        .fileExt(kbDocument.getFileExt())
                        .fileSha256(kbDocument.getFileSha256())
                        .indexTaskNo(indexTask.getTaskNo())
                        .taskId(indexTask.getId())
                        .build()
        );
        // TODO objectKey 合法性校验（通过 OSS HeadObject 校验）
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteDocument(Long documentId) {
        // 判断权限
        Long userId = UserContext.getUserId();
        KbDocument document = getById(documentId);
        SysUser user = sysUserService.getById(userId);
        if (document == null) {
            return Result.fail("文档不存在");
        }
        if (user == null) {
            return Result.fail("用户不存在");
        }
        boolean isOwner = document.getTeacherId() != null && document.getTeacherId().equals(userId);
        boolean isAdmin = user.getRole() == UserType.ADMIN;
        if (!isOwner && !isAdmin) {
            return Result.fail("您没有权限删除该文档");
        }
        // 删除文档(逻辑删除) -> 包括切片和向量
        lambdaUpdate()
                .eq(KbDocument::getId, documentId)
                .set(KbDocument::getIsDeleted, 1)
                .update();

        kbChunkMapper.update(
                null,
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<KbChunk>lambdaUpdate()
                        .eq(KbChunk::getDocId, documentId)
                        .set(KbChunk::getIsDeleted, 1)
        );

        kbChunkEmbeddingMapper.update(
                null,
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<KbChunkEmbedding>lambdaUpdate()
                        .eq(KbChunkEmbedding::getDocId, documentId)
                        .set(KbChunkEmbedding::getIsDeleted, 1)
        );

        indexTaskService.lambdaUpdate()
                .eq(KbIndexTask::getDocId, documentId)
                .set(KbIndexTask::getIsDeleted, 1)
                .update();

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> restoreDocument(Long documentId) {
        // 判断权限
        Long userId = UserContext.getUserId();
        KbDocument document = getById(documentId);
        SysUser user = sysUserService.getById(userId);
        if (document == null) {
            return Result.fail("文档不存在");
        }
        if (user == null) {
            return Result.fail("用户不存在");
        }
        boolean isOwner = document.getTeacherId() != null && document.getTeacherId().equals(userId);
        boolean isAdmin = user.getRole() == UserType.ADMIN;
        if (!isOwner && !isAdmin) {
            return Result.fail("您没有权限恢复该文档");
        }
        // 恢复文档 -> 包括切片和向量
        lambdaUpdate()
                .eq(KbDocument::getId, documentId)
                .set(KbDocument::getIsDeleted, 0)
                .update();

        kbChunkMapper.update(
                null,
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<KbChunk>lambdaUpdate()
                        .eq(KbChunk::getDocId, documentId)
                        .set(KbChunk::getIsDeleted, 0)
        );

        kbChunkEmbeddingMapper.update(
                null,
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<KbChunkEmbedding>lambdaUpdate()
                        .eq(KbChunkEmbedding::getDocId, documentId)
                        .set(KbChunkEmbedding::getIsDeleted, 0)
        );

        indexTaskService.lambdaUpdate()
                .eq(KbIndexTask::getDocId, documentId)
                .set(KbIndexTask::getIsDeleted, 0)
                .update();

        return Result.success();
    }

    @Override
    public Result<PageResult<DocumentVO>> getDocumentList(DocumentPageQuery pageQuery) {
        Page<KbDocument> page = lambdaQuery()
                .eq(pageQuery.getCourseId() != null, KbDocument::getCourseId, pageQuery.getCourseId())
                .eq(pageQuery.getTeacherId() != null, KbDocument::getTeacherId, pageQuery.getTeacherId())
                .eq(pageQuery.getDocumentType() != null, KbDocument::getModality, pageQuery.getDocumentType())
                .eq(pageQuery.getLanguage() != null, KbDocument::getLanguage, pageQuery.getLanguage())
                .eq(KbDocument::getIsDeleted, 0)
                .orderByDesc(KbDocument::getUpdatedAt)
                .page(pageQuery.toPage());

        if (page == null || page.getRecords() == null) {
            return Result.success(PageResult.of(0, 0, Collections.emptyList()));
        }

        // 组装vo
        List<DocumentVO> documentVOS = page.getRecords().stream()
                .map(document -> {
                    return DocumentVO.builder()
                            .documentId(document.getId())
                            .documentName(document.getTitle())
                            .documentUrl(aliOssUtil.signGetUrl(document.getStorageKey(), OSS_EXPIRE))
                            .documentType(document.getModality())
                            .uploaderId(document.getTeacherId())
                            .uploaderName(sysUserService.getById(document.getTeacherId()).getUsername())
                            .uploadTime(document.getCreatedAt())
                            .courseId(document.getCourseId())
                            .courseName(courseService.getById(document.getCourseId()).getCourseName())
                            .collegeId(document.getCourseId())
                            .collegeName(collegeService.getById(document.getCourseId()).getName())
                            .build();
                })
                .toList();

        return Result.success(PageResult.of(page.getTotal(), page.getSize(), documentVOS));
    }

    /**
     * 在课程范围内判断是否与已有未删除文档重复，用于上传去重。
     * <p>
     * 匹配策略（由强到弱）：
     * <ol>
     *   <li>{@code fileSha256} 非空：按内容摘要精确匹配
     *   <li>否则按 {@code storageKey}：同一课程下同一 ObjectKey 视为同一次上传；
     * </ol>
     * </p>
     *
     * @param fileSha256      文件 SHA-256 十六进制串，可为空
     * @param storageKey      OSS ObjectKey
     * @return 已存在的文档，不存在则 empty
     */
    private Optional<KbDocument> findExistingDuplicateInCourse(
            Long courseId, String fileSha256, String storageKey) {
        if (courseId == null) {
            return Optional.empty();
        }
        if (fileSha256 != null && !fileSha256.isBlank()) {
            KbDocument byHash = lambdaQuery()
                    .eq(KbDocument::getCourseId, courseId)
                    .eq(KbDocument::getFileSha256, normalizeSha256Hex(fileSha256))
                    .eq(KbDocument::getIsDeleted, 0)
                    .last("LIMIT 1")
                    .one();
            return Optional.ofNullable(byHash);
        }
        if (storageKey != null && !storageKey.isBlank()) {
            KbDocument byKey = lambdaQuery()
                    .eq(KbDocument::getCourseId, courseId)
                    .eq(KbDocument::getStorageKey, storageKey.trim())
                    .eq(KbDocument::getIsDeleted, 0)
                    .last("LIMIT 1")
                    .one();
            if (byKey != null) {
                return Optional.of(byKey);
            }
        }
        return Optional.empty();
    }

    /**
     * 统一 SHA-256 字符串形式（小写、去空白），避免同一文件因大小写不同被当成两条。
     */
    private static String normalizeSha256Hex(String fileSha256) {
        return fileSha256.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * 转为 {@link MimeTypeEnum}；无法匹配时按扩展名推断，最后退回二进制流。
     */
    private static MimeTypeEnum resolveMimeType(String fileType, String fileExt) {
        String ft = (fileType == null ? "" : fileType).trim();
        for (MimeTypeEnum e : MimeTypeEnum.values()) {
            if (e.getValue().equalsIgnoreCase(ft)) {
                return e;
            }
        }
        String ext = (fileExt == null ? "" : fileExt).toLowerCase(Locale.ROOT);
        return switch (ext) {
            case "pdf" -> MimeTypeEnum.PDF;
            case "doc" -> MimeTypeEnum.DOC;
            case "docx" -> MimeTypeEnum.DOCX;
            case "ppt" -> MimeTypeEnum.PPT;
            case "pptx" -> MimeTypeEnum.PPTX;
            case "md", "markdown" -> MimeTypeEnum.MARKDOWN;
            case "txt" -> MimeTypeEnum.TXT;
            case "png" -> MimeTypeEnum.PNG;
            case "jpg" -> MimeTypeEnum.JPG;
            case "jpeg" -> MimeTypeEnum.JPEG;
            case "mp3" -> MimeTypeEnum.MP3;
            case "mp4" -> MimeTypeEnum.MP4;
            case "json" -> MimeTypeEnum.JSON;
            default -> MimeTypeEnum.OCTET_STREAM;
        };
    }

    /**
     * 计算 SHA-256 哈希值（十六进制串）
     * @param content
     * @return
     * @throws Exception
     */
    public static String sha256(String content) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(content.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
