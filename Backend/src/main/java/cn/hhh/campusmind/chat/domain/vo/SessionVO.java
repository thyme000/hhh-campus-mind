package cn.hhh.campusmind.chat.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionVO {
    /** 会话id */
    private Long sessionId;
    /** 会话标题 */
    private String title;
    /** 会话创建时间 */
    private LocalDateTime createAt;
}
