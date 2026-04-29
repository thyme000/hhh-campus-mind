package cn.hhh.campusmind.resources.domain.po;

import java.math.BigDecimal;
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
 * 知识库切分块表（RAG 检索主数据）
 * </p>
 *
 * @author thyme
 * @since 2026-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("kb_chunk")
public class KbChunk implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 业务chunk编号（唯一）
     */
    private String chunkNo;

    /**
     * 关联文档ID（kb_document.id）
     */
    private Long docId;

    /**
     * 关联索引任务ID（kb_index_task.id）
     */
    private Long taskId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 章节ID（可选）
     */
    private Long chapterId;

    /**
     * 知识点ID（可选）
     */
    private Long knowledgePointId;

    /**
     * chunk顺序号（从0开始）
     */
    private Integer chunkIndex;

    /**
     * chunk正文
     */
    private String content;

    /**
     * chunk内容hash（去重）
     */
    private String contentHash;

    /**
     * token估算数量
     */
    private Integer tokenCount;

    /**
     * 字符数量
     */
    private Integer charCount;

    /**
     * 原文起始偏移
     */
    private Integer startOffset;

    /**
     * 原文结束偏移
     */
    private Integer endOffset;

    /**
     * 来源页码
     */
    private Integer pageNo;

    /**
     * 模态：TEXT/IMAGE/TABLE/CODE/OCR
     */
    private String modality;

    /**
     * 来源类型
     */
    private String sourceType;

    /**
     * 来源定位引用（如 fileKey#page=3）
     */
    private String sourceRef;

    /**
     * 语言
     */
    private String language;

    /**
     * 向量状态：PENDING/SUCCESS/FAILED
     */
    private String vectorStatus;

    /**
     * 质量分（0-100可映射到0.00-100.00）
     */
    private BigDecimal qualityScore;

    /**
     * 逻辑删除标记：0否 1是
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
