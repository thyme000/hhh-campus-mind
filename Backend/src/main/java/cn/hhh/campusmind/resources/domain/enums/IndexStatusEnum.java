package cn.hhh.campusmind.resources.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 索引状态枚举
 * 索引状态：PENDING/RUNNING/SUCCESS/FAILED
 */
@Getter
public enum IndexStatusEnum {

    /** 待处理 */
    PENDING("PENDING", "待处理"),

    /** 运行中 */
    RUNNING("RUNNING", "运行中"),

    /** 成功 */
    SUCCESS("SUCCESS", "成功"),

    /** 失败 */
    FAILED("FAILED", "失败");

    @EnumValue
    private final String code;
    private final String name;

    IndexStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}