package cn.hhh.campusmind.chat.domain.vo;

import lombok.Data;

@Data
public class ResultFromSendHttp {
    /** 会话ID */
    private String sessionId;
    /** 消息内容 */
    private String content;
    /** 跟踪ID */
    private String traceId;
    /** 返回类型 */
    private String contentType;
}
