from enum import Enum


class TaskStatusEnum(Enum):
    """
    任务状态枚举类
    对应 Java 中的 TaskStatusEnum
    """
    CREATED = "CREATED"    # 创建
    QUEUED = "QUEUED"      # 排队
    RUNNING = "RUNNING"    # 运行中
    SUCCESS = "SUCCESS"    # 成功
    FAILED = "FAILED"      # 失败
    CANCELLED = "CANCELLED"  # 取消
