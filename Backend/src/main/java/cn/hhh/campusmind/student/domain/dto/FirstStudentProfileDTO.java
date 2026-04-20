package cn.hhh.campusmind.student.domain.dto;

import cn.hhh.campusmind.student.domain.enums.ResourcesTypeEnum;
import lombok.Data;

@Data
public class FirstStudentProfileDTO {

    /**
     * 知识基础/掌握度评分
     */
    private Integer knowledgeLevel;

    /**
     * 偏好哪种资源
     */
    private ResourcesTypeEnum resourcePref;

    /**
     * 学习目标
     */
    private String goalText;

    /**
     * 偏好学习时间段
     */
    private String preferredTimeSlots;
}
