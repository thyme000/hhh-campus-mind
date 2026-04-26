package cn.hhh.campusmind.chat.domain.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateSeesionTitleMessage {
    /** 会话id */
    private Long sessionId;
    /** 用户id */
    private Long userId;
}
