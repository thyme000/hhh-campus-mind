package cn.hhh.campusmind.resources.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum TaskStatusEnum {

    /** 创建 */
    CREATED("CREATED"),
    /** 排队 */
    QUEUED("QUEUED"),
    /** 运行中 */
    RUNNING("RUNNING"),
    /** 成功 */
    SUCCESS("SUCCESS"),
    /** 失败 */
    FAILED("FAILED"),
    /** 取消 */
    CANCELLED("CANCELLED");

    @EnumValue
    private final String value;

    TaskStatusEnum(String value) {
        this.value = value;
    }
}