package cn.hhh.campusmind.chat.domain.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveContextAfterChatMessage {
    /** 会话id */
    private Long sessionId;
    /** 用户发言 */
    private String userMessage;
    /** ai发言 */
    private String aiMessage;
    /** 用户id */
    private Long userId;
    /** 链路id */
    private String traceId;
    /** 内容类型 */
    private String contentType;
}
