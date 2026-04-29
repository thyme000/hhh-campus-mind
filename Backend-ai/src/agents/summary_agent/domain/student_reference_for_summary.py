from dataclasses import dataclass


@dataclass
class StudentReferenceForSummary:
    def __init__(
            self,
            user_id: int | None,  # 用户ID
            event_type: str,  # 触发事件类型
            ref_id: int | None = None,  # 关联业务ID
            course_id: int | None = None,  # 课程ID
            occurred_at: str | None = None,  # 事件发生时间
            current_profile: dict | None = None,  # 当前画像
            latest_snapshots: list[dict] | None = None,  # 最近画像快照
            context: dict | None = None,  # 上下文（对话摘要/测验统计等）
    ):
        self.user_id = user_id
        self.event_type = event_type
        self.ref_id = ref_id
        self.course_id = course_id
        self.occurred_at = occurred_at
        self.current_profile = current_profile or {}
        self.latest_snapshots = latest_snapshots or []
        self.context = context or {}

    def to_dict(self) -> dict:
        return {
            "userId": self.user_id,
            "eventType": self.event_type,
            "refId": self.ref_id,
            "courseId": self.course_id,
            "occurredAt": self.occurred_at,
            "currentProfile": self.current_profile,
            "latestSnapshots": self.latest_snapshots,
            "context": self.context,
        }
