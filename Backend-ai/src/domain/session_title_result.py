from dataclasses import dataclass
from typing import Dict, Any

@dataclass
class SessionTitleResult:
    """会话标题结果"""

    # 会话id
    sessionId: int

    # 会话标题
    title: str

    def to_dict(self) -> dict:
        """转换为字典，用于JSON序列化"""
        return {
            "sessionId": self.sessionId,
            "title": self.title,
        }

    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> "SessionTitleResult":
        """从字典创建实例"""
        return cls(
            sessionId=int(data["sessionId"]),
            title=str(data["title"]),
        )


