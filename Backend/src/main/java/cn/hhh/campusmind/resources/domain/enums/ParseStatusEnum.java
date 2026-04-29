package cn.hhh.campusmind.resources.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 解析状态枚举
 * 解析状态：UPLOADED/PARSING/PARSED/FAILED
 */
@Getter
public enum ParseStatusEnum {

    /** 已上传 */
    UPLOADED("UPLOADED", "已上传"),

    /** 解析中 */
    PARSING("PARSING", "解析中"),

    /** 解析完成 */
    PARSED("PARSED", "解析完成"),

    /** 解析失败 */
    FAILED("FAILED", "解析失败");

    @EnumValue
    private final String code;
    private final String name;

    ParseStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}