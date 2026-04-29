package cn.hhh.campusmind.user.domain.po;

import cn.hhh.campusmind.user.domain.enums.StatusEnum;
import cn.hhh.campusmind.user.domain.enums.UserType;
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
 * 系统用户表（学生/教师/管理员）；登录鉴权主体。
 * </p>
 *
 * @author thyme
 * @since 2026-04-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 登录账号（唯一）
     */
    private String username;

    /**
     * 密码哈希（BCrypt等，不存明文）
     */
    private String passwordHash;

    /**
     * 角色：student/teacher/admin
     */
    private UserType role;

    /**
     * 账号状态：active/disabled/locked
     */
    private StatusEnum status;

    /**
     * 头像URL（OSS objectKey）
     */
    private String avatarUrl;

    /**
     * 手机号（可空，非空时唯一）
     */
    private String phone;

    /**
     * 手机号是否已验证
     */
    private Boolean phoneVerified;

    /**
     * 学院id (可空)
     */
    private Long collegeId;

    /**
     * 最近登录时间
     */
    private LocalDateTime lastLoginAt;

    /**
     * 最近登录IP
     */
    private String lastLoginIp;

    /**
     * 逻辑删除标记：0未删，1已删（与 mybatis-plus 逻辑删除配置一致）
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
