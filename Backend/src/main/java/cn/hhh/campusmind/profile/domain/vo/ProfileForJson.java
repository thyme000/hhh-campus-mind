package cn.hhh.campusmind.profile.domain.vo;

import lombok.Data;

@Data
public class ProfileForJson {

    /** 知识水平 */
    private Integer knowledgeLevel;

    /** 编码水平 */
    private Integer codingLevel;

    /** 学习速度 */
    private Integer learningSpeed;

    /** 动机水平 */
    private Integer motivationLevel;

    /** 文档资源偏好 */
    private Integer resourcePrefDoc;

    /** 视频资源偏好 */
    private Integer resourcePrefVideo;

    /** 代码资源偏好 */
    private Integer resourcePrefCode;

    /** 导图资源偏好 */
    private Integer resourcePrefMindmap;

    /** 目标文本 */
    private String goalText;

    /** 每周学习频率 */
    private Integer studyFrequencyWeek;

    /** 偏好时间段 */
    private String preferredTimeSlots;

    /** 薄弱知识点ID */
    private String weakKpIds;

    /** 错误类型统计 */
    private String mistakeTypeStats;
}