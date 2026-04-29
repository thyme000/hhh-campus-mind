package cn.hhh.campusmind.chat.domain.vo;

import cn.hhh.campusmind.chat.domain.enums.RoleEnum;
import lombok.Data;

@Data
public class ChatMessageVO {
    /** 会话ID */
    private Long sessionId;
    /** 消息ID */
    private Long messageId;
    /** 角色 */
    private RoleEnum role;
    /** 消息内容 */
    private String content;
    /** 创建时间 */
    private String createAt;
}
