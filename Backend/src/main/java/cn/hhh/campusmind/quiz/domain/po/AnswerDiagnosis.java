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
 * 作答诊断结果（错因+错点知识点）
 * </p>
 *
 * @author thyme
 * @since 2026-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("answer_diagnosis")
public class AnswerDiagnosis implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 诊断记录ID（UUID）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 作答记录ID
     */
    private String attemptId;

    /**
     * 错因类型（concept/method/calc/constraint/format/careless/unknown）
     */
    private String errorType;

    /**
     * 错的知识点ID列表（更细粒度的归因，优先用于统计）
     */
    private String errorKpIds;

    /**
     * 证据（命中规则、模型片段、对比点等）
     */
    private String evidence;

    /**
     * 诊断模型/策略名（可空字符串）
     */
    private String modelName;

    /**
     * 模型版本（可空字符串）
     */
    private String modelVersion;

    /**
     * 置信度（0~1）
     */
    private BigDecimal confidence;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;


}
