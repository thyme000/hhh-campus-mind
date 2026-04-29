package cn.hhh.campusmind.chat.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageDTO {
    /** 会话ID */
    @NotNull(message = "会话ID不能为空")
    private Long sessionId;
    /**消息内容 */
    @NotBlank(message = "消息内容不能为空")
    private String content;
    /** 链路ID */
    @NotNull(message = "链路ID不能为空")
    private String traceId;
}