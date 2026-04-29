package cn.hhh.campusmind.resources.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 文档类型枚举
 * 主模态：TEXT/IMAGE/AUDIO/VIDEO/MIXED
 */
@Getter
public enum DocumentTypeEnum {

    /** 文本类型 */
    TEXT("TEXT", "文本"),

    /** 图片类型 */
    IMAGE("IMAGE", "图片"),

    /** TODO(暂时没有)音频类型 */
    AUDIO("AUDIO", "音频"),

    /** 视频类型 */
    VIDEO("VIDEO", "视频"),

    /** 混合类型 */
    MIXED("MIXED", "混合");

    @EnumValue
    private final String code;
    private final String name;

    DocumentTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
