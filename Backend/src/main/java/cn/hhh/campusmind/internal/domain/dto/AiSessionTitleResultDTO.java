package cn.hhh.campusmind.internal.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiSessionTitleResultDTO {
    /** 会话标题 */
    @NotBlank(message = "会话标题不能为空")
    private String title;
    /** 会话ID */
    @NotNull(message = "会话ID不能为空")
    private Long sessionId;
}
