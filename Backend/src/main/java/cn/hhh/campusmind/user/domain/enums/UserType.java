package cn.hhh.campusmind.user.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 用户类型枚举
 */
public enum UserType {

    /**
     * 学生
     */
    STUDENT("student", "学生"),

    /**
     * 教师
     */
    TEACHER("teacher", "教师"),

    /**
     * 管理员
     */
    ADMIN("admin", "管理员");

    @EnumValue
    private final String code;
    private final String name;

    UserType(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
