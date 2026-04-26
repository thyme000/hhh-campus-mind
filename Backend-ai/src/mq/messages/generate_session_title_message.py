from dataclasses import dataclass


@dataclass
class GenerateSessionTitleMessage:
    """生成会话标题消息"""

    # 会话id
    sessionId: int

    # 用户id
    userId: int

    def to_dict(self) -> dict:
        """转换为字典，用于JSON序列化"""
        return {
            "sessionId": self.sessionId,
            "userId": self.userId
        }

    @classmethod
    def from_dict(cls, data: dict) -> 'GenerateSessionTitleMessage':
        """从字典创建实例"""
        return cls(
            sessionId=data["sessionId"],
            userId=data["userId"]
        )
