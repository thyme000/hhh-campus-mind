package cn.hhh.campusmind.profile.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 处理状态枚举
 */
@Getter
public enum SnapshotStatusEnum {

    /**
     * 已应用
     */
    APPLIED("applied", "已应用"),

    /**
     * 跳过
     */
    SKIPPED("skipped", "跳过"),

    /**
     * 失败
     */
    FAILED("failed", "失败");

    /**
     * 状态代码
     */
    @EnumValue
    private final String code;

    /**
     * 状态名称
     */
    private final String name;

    SnapshotStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
