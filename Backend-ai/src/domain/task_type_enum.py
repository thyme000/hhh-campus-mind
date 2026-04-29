from enum import Enum


class TaskTypeEnum(Enum):
    """
    任务类型枚举类
    对应 Java 中的 TaskTypeEnum
    """
    DOCUMENT_INDEX = "DOCUMENT_INDEX"  # 文档索引任务