from dataclasses import dataclass, field
from typing import Any, Literal


@dataclass
class ResourcePreferenceSummary:
    doc: int | None = None
    video: int | None = None
    code: int | None = None
    mindmap: int | None = None

    def to_dict(self) -> dict:
        return {
            "doc": self.doc,
            "video": self.video,
            "code": self.code,
            "mindmap": self.mindmap,
        }


@dataclass
class CurrentProfileSummary:
    knowledge_level: int | None = None
    coding_level: int | None = None
    learning_speed: int | None = None
    motivation_level: int | None = None
    study_frequency_week: int | None = None
    resource_preference: ResourcePreferenceSummary = field(default_factory=ResourcePreferenceSummary)
    goal_text: str = ""
    preferred_time_slots: list[Any] = field(default_factory=list)
    weak_kp_ids: list[Any] = field(default_factory=list)
    mistake_type_stats: dict[str, Any] = field(default_factory=dict)

    def to_dict(self) -> dict:
        return {
            "knowledgeLevel": self.knowledge_level,
            "codingLevel": self.coding_level,
            "learningSpeed": self.learning_speed,
            "motivationLevel": self.motivation_level,
            "studyFrequencyWeek": self.study_frequency_week,
            "resourcePreference": self.resource_preference.to_dict(),
            "goalText": self.goal_text,
            "preferredTimeSlots": self.preferred_time_slots,
            "weakKpIds": self.weak_kp_ids,
            "mistakeTypeStats": self.mistake_type_stats,
        }


@dataclass
class HistorySignals:
    recent_snapshot_count: int = 0
    version_trend: Literal["up", "flat", "unknown"] = "unknown"
    stability: Literal["stable", "volatile", "unknown"] = "unknown"
    recent_changed_fields: list[str] = field(default_factory=list)

    def to_dict(self) -> dict:
        return {
            "recentSnapshotCount": self.recent_snapshot_count,
            "versionTrend": self.version_trend,
            "stability": self.stability,
            "recentChangedFields": self.recent_changed_fields,
        }


@dataclass
class EventSignals:
    event_importance: Literal["high", "medium", "low"] = "low"
    related_fields: list[str] = field(default_factory=list)
    risk_hints: list[str] = field(default_factory=list)

    def to_dict(self) -> dict:
        return {
            "eventImportance": self.event_importance,
            "relatedFields": self.related_fields,
            "riskHints": self.risk_hints,
        }


@dataclass
class ContextSummary:
    has_context: bool = False
    key_facts: list[str] = field(default_factory=list)
    uncertainties: list[str] = field(default_factory=list)

    def to_dict(self) -> dict:
        return {
            "hasContext": self.has_context,
            "keyFacts": self.key_facts,
            "uncertainties": self.uncertainties,
        }


@dataclass
class SummaryConfidence:
    overall: float = 0.0

    def to_dict(self) -> dict:
        return {"overall": self.overall}


@dataclass
class RecommendationForDiagnostic:
    need_further_context: bool = False
    focus_fields: list[str] = field(default_factory=list)
    suggested_action: Literal["update", "skip", "need_more_evidence"] = "need_more_evidence"

    def to_dict(self) -> dict:
        return {
            "needFurtherContext": self.need_further_context,
            "focusFields": self.focus_fields,
            "suggestedAction": self.suggested_action,
        }


@dataclass
class SummaryStudentReferenceResult:
    user_id: int | None
    event_type: str
    ref_id: int | None = None
    course_id: int | None = None
    occurred_at: str | None = None
    profile_version: int | None = None
    current_profile_summary: CurrentProfileSummary = field(default_factory=CurrentProfileSummary)
    history_signals: HistorySignals = field(default_factory=HistorySignals)
    event_signals: EventSignals = field(default_factory=EventSignals)
    context_summary: ContextSummary = field(default_factory=ContextSummary)
    evidence: list[str] = field(default_factory=list)
    summary_confidence: SummaryConfidence = field(default_factory=SummaryConfidence)
    recommendation_for_diagnostic: RecommendationForDiagnostic = field(default_factory=RecommendationForDiagnostic)

    def to_dict(self) -> dict:
        return {
            "userId": self.user_id,
            "eventType": self.event_type,
            "refId": self.ref_id,
            "courseId": self.course_id,
            "occurredAt": self.occurred_at,
            "profileVersion": self.profile_version,
            "currentProfileSummary": self.current_profile_summary.to_dict(),
            "historySignals": self.history_signals.to_dict(),
            "eventSignals": self.event_signals.to_dict(),
            "contextSummary": self.context_summary.to_dict(),
            "evidence": self.evidence,
            "summaryConfidence": self.summary_confidence.to_dict(),
            "recommendationForDiagnostic": self.recommendation_for_diagnostic.to_dict(),
        }
