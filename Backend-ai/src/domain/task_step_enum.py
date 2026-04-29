from enum import Enum


class TaskStepEnum(Enum):
    """
    任务步骤枚举类
    对应 Java 中的 TaskStepEnum
    """
    INIT = "INIT"    # 初始化
    PARSE = "PARSE"  # 解析
    CHUNK = "CHUNK"  # 切分
    EMBED = "EMBED"  # 嵌入
    UPSERT = "UPSERT"  # 插入/更新
    DONE = "DONE"    # 完成