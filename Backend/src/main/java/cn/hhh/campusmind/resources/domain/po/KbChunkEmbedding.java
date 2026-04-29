package cn.hhh.campusmind.resources.domain.po;

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
 * 知识库切分块向量表（pgvector）
 * </p>
 *
 * @author thyme
 * @since 2026-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("kb_chunk_embedding")
public class KbChunkEmbedding implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联 kb_chunk.id
     */
    private Long chunkId;

    /**
     * 文档ID冗余字段
     */
    private Long docId;

    /**
     * 课程ID冗余字段
     */
    private Long courseId;

    /**
     * 向量维度
     */
    private Integer embeddingDim;

    /**
     * 向量值
     */
    private String embedding;

    /**
     * 逻辑删除标记：0否 1是
     */
    private Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
