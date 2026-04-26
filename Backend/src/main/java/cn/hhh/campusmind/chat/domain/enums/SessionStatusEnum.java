package cn.hhh.campusmind.chat.domain.enums;


import lombok.Getter;

/**
 * 会话状态枚举
 * 1: 进行中
 * 2: 已归档
 * 0: 禁用
 */
@Getter
public enum SessionStatusEnum {

    /**
     * 禁用
     */
    DISABLED("disabled", "禁用"),

    /**
     * 进行中
     */
    ACTIVE("active", "进行中"),

    /**
     * 已归档
     */
    ARCHIVED("archived", "已归档");

    private final String status;
    private final String description;

    SessionStatusEnum(String status, String description) {
        this.status = status;
        this.description = description;
    }

}

