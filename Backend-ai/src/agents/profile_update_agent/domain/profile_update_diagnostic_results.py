from dataclasses import dataclass


@dataclass
class ProfileUpdateDiagnosticResult:
    def __init__(
            self,
            user_id: int | None, # 用户ID
            profile_version: int | None, # 用户画像版本
            event_type: str, # 事件类型
            should_update: bool = False, # 是否更新用户画像
            reason: str = "", # 更新或不更新原因
            delta: dict | None = None, # 更新内容
            evidence: list[str] | None = None, # 证据
            confidence: dict | None = None, # 置信度
    ):
        self.user_id = user_id
        self.profile_version = profile_version
        self.event_type = event_type
        self.should_update = should_update
        self.reason = reason
        self.delta = delta or {}
        self.evidence = evidence or []
        self.confidence = confidence or {}

    def to_dict(self) -> dict:
        return {
            "userId": self.user_id,
            "profileVersion": self.profile_version,
            "eventType": self.event_type,
            "shouldUpdate": self.should_update,
            "reason": self.reason,
            "delta": self.delta,
            "evidence": self.evidence,
            "confidence": self.confidence,
        }

