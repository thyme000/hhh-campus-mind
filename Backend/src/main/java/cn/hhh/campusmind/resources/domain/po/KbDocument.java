package cn.hhh.campusmind.resources.domain.po;

import cn.hhh.campusmind.resources.domain.enums.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 知识库文档主表（老师上传文件后落库）
 * </p>
 *
 * @author thyme
 * @since 2026-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("kb_document")
public class KbDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 业务文档编号（唯一）
     */
    private String docNo;

    /**
     * 上传教师ID（关联用户表）
     */
    private Long teacherId;

    /**
     * 所属课程ID
     */
    private Long courseId;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 原始文件名
     */
    private String originalFilename;

    /**
     * 对象存储Key
     */
    private String storageKey;

    /**
     * 文件后缀（pdf/docx/ppt/pptx/md/markdown）
     */
    private FileExtEnum fileExt;

    /**
     * MIME类型
     */
    private MimeTypeEnum mimeType;

    /**
     * 文件大小（字节）
     */
    private Long fileSizeBytes;

    /**
     * 文件SHA256摘要（去重用）
     */
    private String fileSha256;

    /**
     * 来源类型：UPLOAD/IMPORT/CRAWL
     */
    private SourceTypeEnum sourceType;

    /**
     * 主模态：TEXT/IMAGE/AUDIO/VIDEO/MIXED
     */
    private DocumentTypeEnum modality;

    /**
     * 文档语言，如 Chinese
     */
    private String language;

    /**
     * 切分chunk数量
     */
    private Integer chunkCount;

    /**
     * 向量条数
     */
    private Integer vectorCount;

    /**
     * 版本号
     */
    private Integer versionNo;

    /**
     * 逻辑删除标记：0否 1是
     */
    private Integer isDeleted;

    /**
     * 错误信息（解析或索引失败）
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
