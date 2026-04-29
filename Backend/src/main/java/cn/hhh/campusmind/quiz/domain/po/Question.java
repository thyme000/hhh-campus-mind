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
 * 题目表
 * </p>
 *
 * @author thyme
 * @since 2026-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("question")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 题目ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 外部题库ID/对接ID（可空字符串）
     */
    private String externalId;

    /**
     * 学科/领域
     */
    private String subject;

    /**
     * 题型（single/multi/fill/essay/code/unknown）
     */
    private String qtype;

    /**
     * 难度（0~5或自定义标度）
     */
    private BigDecimal difficulty;

    /**
     * 题干
     */
    private String stem;

    /**
     * 选项（选择题），数组/对象均可
     */
    private String optionsJson;

    /**
     * 标准答案（结构化）
     */
    private String answerKeyJson;

    /**
     * 解析（可选）
     */
    private String explanation;

    /**
     * 是否启用
     */
    private Boolean isActive;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间（需要应用层或触发器维护）
     */
    private LocalDateTime updatedAt;


}
