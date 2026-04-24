package cn.hhh.campusmind.profile.domain.po;

import cn.hhh.campusmind.common.handler.JsonbTypeHandler;
import cn.hhh.campusmind.profile.domain.enums.SnapshotStatusEnum;
import cn.hhh.campusmind.profile.domain.enums.TriggerTypeEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 学生画像更新快照表：记录每次画像更新的完整结果、变更delta、证据与置信度，便于回溯与可解释展示。
 * </p>
 *
 * @author thyme
 * @since 2026-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "profile_snapshot", autoResultMap = true)
public class ProfileSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID（sys_user.id）
     */
    private Long userId;

    /**
     * 画像版本号（与 student_profile.profile_version 对齐）
     */
    private Long profileVersion;

    /**
     * 触发类型：onboarding/chat/quiz/feedback/plan/manual
     */
    private TriggerTypeEnum triggerType;

    /**
     * 触发来源关联ID（如 chatId/attemptId/resourceId/planItemId；无则空）
     */
    private String triggerRefId;

    /**
     * 本次更新的差异（JSON对象：哪些字段从什么变成什么）
     */
    @TableField(typeHandler = JsonbTypeHandler.class, jdbcType = JdbcType.OTHER)
    private String delta;

    /**
     * 本次更新后的画像完整结构（JSON对象，可存当时的汇总）
     */
    @TableField(typeHandler = JsonbTypeHandler.class, jdbcType = JdbcType.OTHER)
    private String profile;

    /**
     * 证据来源（JSON数组：对话片段/练习统计摘要等）
     */
    @TableField(typeHandler = JsonbTypeHandler.class, jdbcType = JdbcType.OTHER)
    private String evidence;

    /**
     * 置信度（JSON对象：各维度0~1）
     */
    @TableField(typeHandler = JsonbTypeHandler.class, jdbcType = JdbcType.OTHER)
    private String confidence;

    /**
     * 处理状态：applied已应用 / skipped跳过 / failed失败
     */
    private SnapshotStatusEnum status;

    /**
     * 更新原因或不更新的原因
     */
    private String reason;

    /**
     * 创建时间（无时区，匹配 Java LocalDateTime）
     */
    private LocalDateTime createdAt;


}
