package cn.hhh.campusmind.chat.domain.enums;

import lombok.Getter;

/**
 * 消息角色枚举
 */
@Getter
public enum RoleEnum {
    /**
     * 用户角色
     */
    USER("user"),

    /**
     * AI角色
     */
    AI("ai");

    private final String code;

    RoleEnum(String code) {
        this.code = code;
    }
}

