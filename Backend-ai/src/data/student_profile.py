from datetime import datetime

from sqlalchemy import BigInteger, DateTime, Integer, Text
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column


class Base(DeclarativeBase):
    pass

# 学生画像实体类
class StudentProfile(Base):
    """
    - id
    - user_id
    - knowledge_level
    - coding_level
    - learning_speed
    - motivation_level
    - resource_pref_doc
    - resource_pref_video
    - resource_pref_code
    - resource_pref_mindmap
    - goal_text
    - study_frequency_week
    - preferred_time_slots
    - weak_kp_ids
    - mistake_type_stats
    - evidence
    - confidence
    - profile_version
    - is_delete
    - created_at
    - updated_at
    """

    __tablename__ = "student_profile"

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True)
    user_id: Mapped[int] = mapped_column(BigInteger)

    knowledge_level: Mapped[int] = mapped_column(Integer, default=50)
    coding_level: Mapped[int] = mapped_column(Integer, default=50)
    learning_speed: Mapped[int] = mapped_column(Integer, default=50)
    motivation_level: Mapped[int] = mapped_column(Integer, default=50)

    resource_pref_doc: Mapped[int] = mapped_column(Integer, default=25)
    resource_pref_video: Mapped[int] = mapped_column(Integer, default=25)
    resource_pref_code: Mapped[int] = mapped_column(Integer, default=25)
    resource_pref_mindmap: Mapped[int] = mapped_column(Integer, default=25)

    goal_text: Mapped[str] = mapped_column(Text, default="")
    study_frequency_week: Mapped[int] = mapped_column(Integer, default=3)

    # PostgreSQL 中对应 jsonb，Python 侧按字符串承载 JSON 文本
    preferred_time_slots: Mapped[str] = mapped_column(Text, default="[]")
    weak_kp_ids: Mapped[str] = mapped_column(Text, default="[]")
    mistake_type_stats: Mapped[str] = mapped_column(Text, default="{}")
    evidence: Mapped[str] = mapped_column(Text, default="[]")
    confidence: Mapped[str] = mapped_column(Text, default="{}")

    profile_version: Mapped[int] = mapped_column(BigInteger, default=0)
    is_delete: Mapped[int] = mapped_column(Integer, default=0)

    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.now)
    updated_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.now)


def student_profile_to_dict(s: StudentProfile) -> dict:
    return {
        "id": s.id,
        "userId": s.user_id,
        "knowledgeLevel": s.knowledge_level,
        "codingLevel": s.coding_level,
        "learningSpeed": s.learning_speed,
        "motivationLevel": s.motivation_level,
        "resourcePrefDoc": s.resource_pref_doc,
        "resourcePrefVideo": s.resource_pref_video,
        "resourcePrefCode": s.resource_pref_code,
        "resourcePrefMindmap": s.resource_pref_mindmap,
        "goalText": s.goal_text,
        "studyFrequencyWeek": s.study_frequency_week,
        "preferredTimeSlots": s.preferred_time_slots,
        "weakKpIds": s.weak_kp_ids,
        "mistakeTypeStats": s.mistake_type_stats,
        "evidence": s.evidence,
        "confidence": s.confidence,
        "profileVersion": s.profile_version,
        "isDelete": s.is_delete,
        "createdAt": s.created_at.isoformat() if s.created_at else None,
        "updatedAt": s.updated_at.isoformat() if s.updated_at else None,
    }
