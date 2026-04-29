from enum import Enum

class IndexStatusEnum(Enum):
    """
    索引状态枚举类
    对应数据库 index_status 字段
    """
    PENDING = "PENDING"  # 待处理
    RUNNING = "RUNNING"  # 处理中
    SUCCESS = "SUCCESS"  # 成功
    FAILED = "FAILED"    # 失败