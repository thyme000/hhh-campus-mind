package cn.hhh.campusmind.resources.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 来源类型枚举
 * 来源类型：UPLOAD/IMPORT/CRAWL
 */
@Getter
public enum SourceTypeEnum {

    /** 上传 */
    UPLOAD("UPLOAD", "上传"),

    /** 导入 */
    IMPORT("IMPORT", "导入"),

    /** 爬取 */
    CRAWL("CRAWL", "爬取");

    @EnumValue
    private final String code;
    private final String name;

    SourceTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}