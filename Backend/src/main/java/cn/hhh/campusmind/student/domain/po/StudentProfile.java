package cn.hhh.campusmind.student.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 学生画像当前汇总表：每个用户1行；用于个性化推荐、路径规划与展示。
 * </p>
 *
 * @author thyme
 * @since 2026-04-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("student_profile")
public class StudentProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联用户ID（sys_user.id）
     */
    private Long userId;

    /**
     * 知识基础/掌握度评分（0-100），默认50
     */
    private Integer knowledgeLevel;

    /**
     * 技能水平：编程能力评分（0-100），默认50
     */
    private Integer codingLevel;

    /**
     * 学习速度评分（0-100），默认50
     */
    private Integer learningSpeed;

    /**
     * 动机水平评分（0-100），默认50
     */
    private Integer motivationLevel;

    /**
     * 资源偏好：文档（0-100），默认25
     */
    private Integer resourcePrefDoc;

    /**
     * 资源偏好：视频（0-100），默认25
     */
    private Integer resourcePrefVideo;

    /**
     * 资源偏好：代码案例（0-100），默认25
     */
    private Integer resourcePrefCode;

    /**
     * 资源偏好：导图（0-100），默认25
     */
    private Integer resourcePrefMindmap;

    /**
     * 学习目标（对话/问卷采集），默认空
     */
    private String goalText;

    /**
     * 每周学习频次（0-14），默认3
     */
    private Integer studyFrequencyWeek;

    /**
     * 偏好学习时间段（JSON数组，如[\"evening\",\"weekend\"]），默认[]
     */
    private String preferredTimeSlots;

    /**
     * 薄弱知识点ID列表（JSON数组），默认[]
     */
    private String weakKpIds;

    /**
     * 错因分布统计（JSON对象，如{\"concept\":3,\"calculation\":1}），默认{}
     */
    private String mistakeTypeStats;

    /**
     * 证据来源（JSON数组：对话片段/练习统计摘要等），用于可解释展示
     */
    private String evidence;

    /**
     * 各维度置信度（JSON对象），用于展示与降级策略
     */
    private String confidence;

    /**
     * 画像版本号（每次更新+1）
     */
    private Long profileVersion;

    /**
     * 逻辑删除：0未删，1已删
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
