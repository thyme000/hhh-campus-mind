package cn.hhh.campusmind.profile.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 触发类型枚举
 */
@Getter
public enum TriggerTypeEnum {

    /**
     * 初始引导
     */
    ONBOARDING("onboarding", "初始引导"),

    /**
     * 聊天
     */
    CHAT("chat", "聊天"),

    /**
     * 测验
     */
    QUIZ("quiz", "测验"),

    /**
     * 反馈
     */
    FEEDBACK("feedback", "反馈"),

    /**
     * 计划
     */
    PLAN("plan", "计划"),

    /**
     * 手动
     */
    MANUAL("manual", "手动");

    /**
     * 触发类型代码
     */
    @EnumValue
    private final String code;

    /**
     * 触发类型名称
     */
    private final String name;

    TriggerTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
