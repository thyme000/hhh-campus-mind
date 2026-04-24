package cn.hhh.campusmind.profile.domain.messages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class TriggerUpdateEventMessage {

    /** 学生id */
    @NotNull(message = "学生id不能为空")
    private Long userId;

    /** 事件类型 */
    @NotBlank(message = "事件类型不能为空")
    private String eventType;

//    /** 关联业务id */
//    @NotNull(message = "关联业务id不能为空")
//    private Long refId;

//    /** 学生上下文摘要 */
//    @NotBlank(message = "学生上下文摘要不能为空")
//    private String contextHint;

    /** 画像版本号 */
    @NotNull(message = "画像版本号不能为空")
    private Long profileVersion;

    /** 事件发生时间 */
    @NotNull(message = "事件发生时间不能为空")
    private LocalDateTime occurredAt;
}
