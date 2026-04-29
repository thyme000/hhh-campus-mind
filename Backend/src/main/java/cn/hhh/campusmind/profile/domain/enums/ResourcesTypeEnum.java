package cn.hhh.campusmind.profile.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 资源类型枚举
 */
@Getter
public enum ResourcesTypeEnum {

    /**
     * 文档
     */
    DOC("resourcePrefDoc", "文档"),

    /**
     * 视频
     */
    VIDEO("resourcePrefVideo", "视频"),

    /**
     * 代码案例
     */
    CODE("resourcePrefCode", "代码案例"),

    /**
     * 导图
     */
    MINDMAP("resourcePrefMindmap", "导图");

    /**
     * 字段名
     */
    @EnumValue
    private final String fieldName;

    /**
     * 类型名称
     */
    private final String typeName;


    ResourcesTypeEnum(String fieldName, String typeName) {
        this.fieldName = fieldName;
        this.typeName = typeName;
    }

}