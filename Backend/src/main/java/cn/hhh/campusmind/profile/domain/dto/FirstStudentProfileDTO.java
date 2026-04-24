package cn.hhh.campusmind.profile.domain.dto;

import cn.hhh.campusmind.profile.domain.enums.ResourcesTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FirstStudentProfileDTO {

    /**
     * 知识基础/掌握度评分
     */
    @NotNull(message = "知识基础/掌握度评分不能为空")
    private Integer knowledgeLevel;

    /**
     * 偏好哪种资源
     */
    @NotNull(message = "偏好哪种资源不能为空")
    private ResourcesTypeEnum resourcePref;

    /**
     * 学习目标
     */
    @NotBlank(message = "学习目标不能为空")
    private String goalText;

    /**
     * 偏好学习时间段
     */
    @NotBlank(message = "偏好学习时间段不能为空")
    private String preferredTimeSlots;
}
