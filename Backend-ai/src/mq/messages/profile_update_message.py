from dataclasses import dataclass
from datetime import datetime

from src.domain.TriggerTypeEnum import TriggerTypeEnum


@dataclass
class TriggerUpdateEventMessage:
    """触发更新事件消息"""

    # 学生id
    userId: int

    # 事件类型
    eventType: TriggerTypeEnum

    # #TODO 学生上下文摘要(暂时不使用)
    # contextHint: str

    # 画像版本号
    profileVersion: int

    # 事件发生时间
    occurredAt: datetime

    def to_dict(self) -> dict:
        """转换为字典，用于JSON序列化"""
        return {
            "userId": self.userId,
            "eventType": self.eventType.value,
            # "contextHint": self.contextHint,
            "profileVersion": self.profileVersion,
            "occurredAt": self.occurredAt.isoformat()
        }

    @classmethod
    def from_dict(cls, data: dict) -> 'TriggerUpdateEventMessage':
        """从字典创建实例"""
        return cls(
            userId=data["userId"],
            eventType=TriggerTypeEnum(data["eventType"]),
            # contextHint=data["contextHint"],
            profileVersion=data["profileVersion"],
            occurredAt=datetime.fromisoformat(data["occurredAt"])
        )

    def get(self, param):
        pass

