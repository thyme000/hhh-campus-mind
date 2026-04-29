package cn.hhh.campusmind.internal.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import cn.hhh.campusmind.profile.domain.vo.Delta;
import java.util.List;
import java.util.Map;

@Data
public class AiProfileResultDTO {

    /** 学生id */
    @NotNull(message = "userId不能为空")
    private Long userId;

    /** 画像版本 */
    private Integer profileVersion;

    /** 触发类型 */
    @NotBlank(message = "eventType不能为空")
    private String eventType;

    /** 目标业务id */
    private Long refId;

    /** 课程id */
    private Long courseId;

    /** 是否建议更新画像 */
    @NotNull(message = "shouldUpdate不能为空")
    private Boolean shouldUpdate;

    /** 建议更新画像的原因 */
    private String reason;

    /** 画像变更delta */
    private Delta delta;

    /** 画像变更证据 */
    private List<String> evidence;

    /** 画像变更置信度 */
    private Map<String, Object> confidence;
}

