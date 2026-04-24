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
 * 用户作答记录表（核心日志）
 * </p>
 *
 * @author thyme
 * @since 2026-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_answer_attempt")
public class UserAnswerAttempt implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 作答记录ID（UUID）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 课程ID（可空）
     */
    private Long courseId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 第几次尝试（同一题同一用户）
     */
    private Integer attemptIndex;

    /**
     * 来源（quiz/homework/exam/practice等）
     */
    private String source;

    /**
     * 开始作答时间（可空）
     */
    private LocalDateTime startedAt;

    /**
     * 提交时间（可空）
     */
    private LocalDateTime submittedAt;

    /**
     * 耗时毫秒（可由 submitted-started 得出或前端上报）
     */
    private Integer timeSpentMs;

    /**
     * 用户提交答案（结构化）
     */
    private String submittedAnswer;

    /**
     * 用户答案原文（非结构化，便于回放与LLM诊断）
     */
    private String answerText;

    /**
     * 是否正确
     */
    private Boolean isCorrect;

    /**
     * 得分
     */
    private BigDecimal score;

    /**
     * 满分
     */
    private BigDecimal maxScore;

    /**
     * 判分来源（auto/manual/llm/mixed）
     */
    private String grader;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间（需要应用层或触发器维护）
     */
    private LocalDateTime updatedAt;


}
