import json

from langchain.agents import create_agent

from domain.TriggerTypeEnum import TriggerTypeEnum
from src.agents.profile_update_agent.tools.get_student_reference import get_student_profile, list_latest_snapshots
from src.agents.summary_agent.domain.student_reference_for_summary import StudentReferenceForSummary
from src.agents.summary_agent.domain.summary_student_reference_result import (
    ContextSummary,
    CurrentProfileSummary,
    EventSignals,
    HistorySignals,
    RecommendationForDiagnostic,
    ResourcePreferenceSummary,
    SummaryConfidence,
    SummaryStudentReferenceResult,
)
from src.common.factory import chat_model
from src.common.logger_handler import logger
from src.utils.prompt_loader import load_prompt


def _safe_parse_json(content: str) -> dict:
    if not content:
        return {}
    try:
        return json.loads(content)
    except Exception:
        pass
    try:
        start = content.find("{")
        end = content.rfind("}")
        if start != -1 and end != -1 and end > start:
            return json.loads(content[start: end + 1])
    except Exception:
        pass
    return {}


def get_student_reference(user_id: int, trigger_event: TriggerTypeEnum, ref_id: int | None = None,
                          course_id: int | None = None, occurred_at: str | None = None) -> SummaryStudentReferenceResult:
    """
    获取学生参考总结
    :param user_id: 用户id
    :param trigger_event: 触发事件类型
    :param ref_id: 关联业务ID
    :param course_id: 课程ID
    :param occurred_at: 事件发生时间
    :return: 学生参考总结
    """
    # 获取学生画像快照和学生画像和上下文
    snapshots = list_latest_snapshots(user_id)
    profile = get_student_profile(user_id)
    context = {}
    student_reference_for_summary = StudentReferenceForSummary(
        user_id = user_id,
        current_profile = profile,
        latest_snapshots = snapshots,
        context = context,
        event_type = trigger_event.value,
        ref_id = ref_id,
        course_id = course_id,
        occurred_at = occurred_at,
    )
    agent = create_agent(
        model=chat_model,
        tools=[],
        system_prompt=load_prompt("get_student_reference_prompt.txt"),
    )
    logger.info("开始总结学生参照")
    result = agent.invoke(
        {
            "messages": [
                {
                    "role": "user",
                    "content": json.dumps(student_reference_for_summary.to_dict(), ensure_ascii=False),
                }
            ]
        }
    )
    messages = result.get("messages") or []

    logger.info(f"学生参考总结结果: {result}")

    content = ""
    for m in reversed(messages):
        c = getattr(m, "content", None)
        if isinstance(c, str) and c.strip():
            content = c.strip()
            break
    parsed: dict = _safe_parse_json(content)

    if not isinstance(parsed, dict) or not parsed:
        parsed = {
            "userId": user_id,
            "eventType": trigger_event.value,
            "refId": ref_id,
            "courseId": course_id,
            "occurredAt": occurred_at,
            "profileVersion": profile.get("profileVersion") if isinstance(profile, dict) else None,
            "currentProfileSummary": {},
            "historySignals": {},
            "eventSignals": {},
            "contextSummary": {
                "hasContext": False,
                "keyFacts": [],
                "uncertainties": ["invalid_model_output"],
            },
            "evidence": [],
            "summaryConfidence": {"overall": 0.0},
            "recommendationForDiagnostic": {
                "needFurtherContext": True,
                "focusFields": [],
                "suggestedAction": "need_more_evidence",
            },
        }

    cps = parsed.get("currentProfileSummary") if isinstance(parsed.get("currentProfileSummary"), dict) else {}
    rp = cps.get("resourcePreference") if isinstance(cps.get("resourcePreference"), dict) else {}
    hs = parsed.get("historySignals") if isinstance(parsed.get("historySignals"), dict) else {}
    es = parsed.get("eventSignals") if isinstance(parsed.get("eventSignals"), dict) else {}
    cs = parsed.get("contextSummary") if isinstance(parsed.get("contextSummary"), dict) else {}
    sc = parsed.get("summaryConfidence") if isinstance(parsed.get("summaryConfidence"), dict) else {}
    rd = parsed.get("recommendationForDiagnostic") if isinstance(parsed.get("recommendationForDiagnostic"), dict) else {}

    return SummaryStudentReferenceResult(
        user_id=parsed.get("userId"),
        event_type=str(parsed.get("eventType") or trigger_event.value),
        ref_id=parsed.get("refId"),
        course_id=parsed.get("courseId"),
        occurred_at=parsed.get("occurredAt"),
        profile_version=parsed.get("profileVersion"),
        current_profile_summary=CurrentProfileSummary(
            knowledge_level=cps.get("knowledgeLevel"),
            coding_level=cps.get("codingLevel"),
            learning_speed=cps.get("learningSpeed"),
            motivation_level=cps.get("motivationLevel"),
            study_frequency_week=cps.get("studyFrequencyWeek"),
            resource_preference=ResourcePreferenceSummary(
                doc=rp.get("doc"),
                video=rp.get("video"),
                code=rp.get("code"),
                mindmap=rp.get("mindmap"),
            ),
            goal_text=str(cps.get("goalText") or ""),
            preferred_time_slots=cps.get("preferredTimeSlots") if isinstance(cps.get("preferredTimeSlots"), list) else [],
            weak_kp_ids=cps.get("weakKpIds") if isinstance(cps.get("weakKpIds"), list) else [],
            mistake_type_stats=cps.get("mistakeTypeStats") if isinstance(cps.get("mistakeTypeStats"), dict) else {},
        ),
        history_signals=HistorySignals(
            recent_snapshot_count=int(hs.get("recentSnapshotCount") or 0),
            version_trend=hs.get("versionTrend") if hs.get("versionTrend") in {"up", "flat", "unknown"} else "unknown",
            stability=hs.get("stability") if hs.get("stability") in {"stable", "volatile", "unknown"} else "unknown",
            recent_changed_fields=hs.get("recentChangedFields") if isinstance(hs.get("recentChangedFields"), list) else [],
        ),
        event_signals=EventSignals(
            event_importance=es.get("eventImportance") if es.get("eventImportance") in {"high", "medium", "low"} else "low",
            related_fields=es.get("relatedFields") if isinstance(es.get("relatedFields"), list) else [],
            risk_hints=es.get("riskHints") if isinstance(es.get("riskHints"), list) else [],
        ),
        context_summary=ContextSummary(
            has_context=bool(cs.get("hasContext", False)),
            key_facts=cs.get("keyFacts") if isinstance(cs.get("keyFacts"), list) else [],
            uncertainties=cs.get("uncertainties") if isinstance(cs.get("uncertainties"), list) else [],
        ),
        evidence=parsed.get("evidence") if isinstance(parsed.get("evidence"), list) else [],
        summary_confidence=SummaryConfidence(
            overall=float(sc.get("overall") or 0.0),
        ),
        recommendation_for_diagnostic=RecommendationForDiagnostic(
            need_further_context=bool(rd.get("needFurtherContext", False)),
            focus_fields=rd.get("focusFields") if isinstance(rd.get("focusFields"), list) else [],
            suggested_action=rd.get("suggestedAction")
            if rd.get("suggestedAction") in {"update", "skip", "need_more_evidence"}
            else "need_more_evidence",
        ),
    )








