from enum import Enum

class TriggerTypeEnum(Enum):
    """事件类型枚举"""
    ONBOARDING = "onboarding"  # 初始引导
    CHAT = "chat"  # 聊天
    QUIZ = "quiz"  # 测验
    FEEDBACK = "feedback"  # 反馈
    PLAN = "plan"  # 计划
    MANUAL = "manual"  # 手动



