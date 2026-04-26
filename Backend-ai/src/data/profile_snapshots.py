from datetime import datetime

from sqlalchemy import BigInteger, DateTime, Text
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column

from src.domain.SnapshotStatusEnum import SnapshotStatusEnum
from src.domain.TriggerTypeEnum import TriggerTypeEnum


class Base(DeclarativeBase):
    pass
# 学生画像快照实体类
class ProfileSnapshot(Base):
    """
    - id
    - user_id
    - profile_version
    - trigger_type
    - trigger_ref_id
    - delta
    - profile
    - evidence
    - confidence
    - status
    - reason
    - created_at
    """
    __tablename__ = "profile_snapshot"

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True)
    user_id: Mapped[int] = mapped_column(BigInteger)
    profile_version: Mapped[int] = mapped_column(BigInteger)
    trigger_type: Mapped[TriggerTypeEnum] = mapped_column(Text)
    trigger_ref_id: Mapped[str] = mapped_column(Text, default="")

    # PostgreSQL 中对应 jsonb，Python 侧按字符串承载 JSON 文本
    delta: Mapped[str] = mapped_column(Text, default="{}")
    profile: Mapped[str] = mapped_column(Text, default="{}")
    evidence: Mapped[str] = mapped_column(Text, default="[]")
    confidence: Mapped[str] = mapped_column(Text, default="{}")

    status: Mapped[SnapshotStatusEnum] = mapped_column(Text, default="applied")
    reason: Mapped[str] = mapped_column(Text, default="")

    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.now)

def snapshot_to_dict(s):
    return {
        "id": s.id,
        "userId": s.user_id,
        "profileVersion": s.profile_version,
        "triggerType": str(s.trigger_type),
        "triggerRefId": s.trigger_ref_id,
        "delta": s.delta,
        "profile": s.profile,
        "evidence": s.evidence,
        "confidence": s.confidence,
        "status": str(s.status),
        "reason": s.reason,
        "createdAt": s.created_at.isoformat() if s.created_at else None,
    }

