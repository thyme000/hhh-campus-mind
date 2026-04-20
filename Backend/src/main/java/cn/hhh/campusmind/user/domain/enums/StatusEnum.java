package cn.hhh.campusmind.user.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 账号状态枚举
 */
public enum StatusEnum {

    /**
     * 活跃
     */
    ACTIVE("active", "活跃"),

    /**
     * 禁用
     */
    DISABLED("disabled", "禁用"),

    /**
     * 锁定
     */
    LOCKED("locked", "锁定");

    @EnumValue
    private final String code;
    private final String name;

    StatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
