package cn.hhh.campusmind.quiz.domain.po;

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
 * 题目-知识点关联表（多对多）
 * </p>
 *
 * @author thyme
 * @since 2026-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("question_knowledge_point")
public class QuestionKnowledgePoint implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 题目ID
     */
    @TableId(value = "question_id", type = IdType.ASSIGN_ID)
    private Long questionId;

    /**
     * 知识点ID
     */
    private Long kpId;

    /**
     * 该题对该知识点权重（多标签时用于分摊统计）
     */
    private BigDecimal weight;

    /**
     * 是否主知识点
     */
    private Boolean isPrimary;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;


}
