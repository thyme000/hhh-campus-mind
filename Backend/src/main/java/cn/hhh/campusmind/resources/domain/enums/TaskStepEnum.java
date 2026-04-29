package cn.hhh.campusmind.resources.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 任务步骤枚举
 */
@Getter
public enum TaskStepEnum {

    /** 初始化 */
    INIT("INIT"),
    /** 解析 */
    PARSE("PARSE"),
    /** 切分 */
    CHUNK("CHUNK"),
    /** 嵌入 */
    EMBED("EMBED"),
    /** 插入/更新 */
    UPSERT("UPSERT"),
    /** 完成 */
    DONE("DONE");

    @EnumValue
    private final String code;

    TaskStepEnum(String code) {
        this.code = code;
    }
}
