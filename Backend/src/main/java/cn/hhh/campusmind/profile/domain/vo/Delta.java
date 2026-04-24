package cn.hhh.campusmind.profile.domain.vo;

import lombok.Data;

import java.util.Map;

@Data
public class Delta {

    /** 事件类型 */
    private String eventType;

    /** 知识基础变化 */
    private Map<String, Object> knowledgeLevel;

    /** 编程能力变化 */
    private Map<String, Object> codingLevel;

    /** 每周学习频率变化 */
    private Map<String, Object> studyFrequencyWeek;

    /** 资源偏好：文档变化 */
    private Map<String, Object> resourcePrefDoc;

    /** 资源偏好：视频变化 */
    private Map<String, Object> resourcePrefVideo;

    /** 资源偏好：代码案例变化 */
    private Map<String, Object> resourcePrefCode;

    /** 资源偏好：导图变化 */
    private Map<String, Object> resourcePrefMindmap;

    /** 画像版本变化 */
    private Map<String, Object> profileVersion;

    /** 学习速度变化 */
    private Map<String, Object> learningSpeed;

    /** 动机水平变化 */
    private Map<String, Object> motivationLevel;

    /** 薄弱知识点ID列表变化 */
    private Map<String, Object> weakKpIds;

    /** 错因分布变化 */
    private Map<String, Object> mistakeTypeStats;

    /** 偏好学习时间段变化 */
    private Map<String, Object> preferredTimeSlots;
}


