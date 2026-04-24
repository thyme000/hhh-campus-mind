package cn.hhh.campusmind.quiz.domain.po;

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
 * 作答步骤记录（用于更精确区分错在第几步）
 * </p>
 *
 * @author thyme
 * @since 2026-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("answer_step")
public class AnswerStep implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 步骤记录ID（UUID）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 作答记录ID
     */
    private String attemptId;


    /**
     * 步骤序号（从1开始）
     */
    private Integer stepNo;

    /**
     * 该步骤文本/中间过程
     */
    private String stepText;

    /**
     * 该步骤是否正确
     */
    private Boolean isCorrect;

    /**
     * 步骤错因类型（同 answer_diagnosis.error_type）
     */
    private String errorType;

    /**
     * 步骤级错点知识点ID列表（可空）
     */
    private String errorKpIds;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;


}
