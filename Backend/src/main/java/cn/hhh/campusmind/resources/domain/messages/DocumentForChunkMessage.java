package cn.hhh.campusmind.resources.domain.messages;

import cn.hhh.campusmind.resources.domain.enums.FileExtEnum;
import cn.hhh.campusmind.resources.domain.enums.MimeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 投递给分片/向量化工作流的文档消息体
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentForChunkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 知识库文档主键 {@code kb_document.id} */
    private Long documentId;

    /** 业务文档编号（唯一，便于对账与幂等） */
    private String docNo;

    /** 上传教师用户 ID */
    private Long teacherId;

    /** 课程 ID，用于分片与检索隔离 */
    private Long courseId;

    /**
     * 对象存储中的 ObjectKey，分片前需从 OSS 拉取原文或二进制
     */
    private String storageKey;

    /** 原始文件名（含扩展名） */
    private String originalFilename;

    /**
     * 文件后缀，如 pdf、docx、pptx、md（小写，与枚举一致时便于 Python 选解析器）
     */
    private FileExtEnum fileExt;

    /** MIME 类型，如 application/pdf */
    private MimeTypeEnum mimeType;

    /** 文件大小（字节） */
    private Long fileSizeBytes;

    /**
     * 文件内容 SHA256，可用于去重与任务幂等；未计算时可传 null
     */
    private String fileSha256;

    /**
     * 所属索引任务单号/ID（若与 {@code kb_index_task} 关联，便于回写状态）
     */
    private String indexTaskNo;

    /** 全链路追踪 ID，与日志/监控对齐 */
    private String traceId;

    /** 任务id */
    private Long taskId;
}
