package cn.hhh.campusmind.chat.domain.vo;

import lombok.Data;

@Data
public class Http4SendMessage {
    /** 会话ID */
    private String sessionId;
    /** 消息内容 */
    private String content;
    /** 用户ID */
    private Long userId;
    /** 跟踪ID */
    private String traceId;
}
