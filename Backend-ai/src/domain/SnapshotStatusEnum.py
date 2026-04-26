from enum import Enum

class SnapshotStatusEnum(Enum):
    """学生画像快照状态枚举"""
    APPLIED = "applied"  # 已应用
    SKIPPED = "skipped"  # 已跳过
    FAILED = "failed"  # 失败
