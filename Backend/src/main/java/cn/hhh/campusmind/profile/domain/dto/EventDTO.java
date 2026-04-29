package cn.hhh.campusmind.profile.domain.dto;

import cn.hhh.campusmind.profile.domain.enums.TriggerTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class EventDTO {

    /** 事件类型 */
    @NotNull(message = "事件类型不能为空")
    private TriggerTypeEnum eventType;

    /** 关联业务id */
    @NotNull(message = "关联业务id不能为空")
    private Long refId;

    /** 课程id */
    @NotNull(message = "课程id不能为空")
    private Long courseId;

    /** 用户做错题目id列表 */
    private List<Long> mistakeQuestionIds;

    /** 所有的题目id列表 */
    private List<Long> allQuestionIds;
}
