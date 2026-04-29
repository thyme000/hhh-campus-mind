package cn.hhh.campusmind.resources.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 任务类型枚举
 */
@Getter
public enum TaskTypeEnum {

    /** 文档索引任务 */
    DOCUMENT_INDEX("DOCUMENT_INDEX");

    @EnumValue
    private final String value;

    TaskTypeEnum(String value) {
        this.value = value;
    }
}