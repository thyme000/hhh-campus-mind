from __future__ import annotations

from typing_extensions import TypedDict
from langgraph.graph import StateGraph, START, END

from src.agents.profile_update_agent.domain.profile_update_diagnostic_results import ProfileUpdateDiagnosticResult
from src.agents.profile_update_agent.profile_update_agent import diagnostic_update_profile
from src.agents.summary_agent.domain.summary_student_reference_result import SummaryStudentReferenceResult
from src.agents.summary_agent.summary_agent import get_student_reference

from src.common.http_client import java_backend_http_client
from src.common.logger_handler import logger
from src.mq.messages.profile_update_message import TriggerUpdateEventMessage


class ProfileUpdateWorkflowState(TypedDict, total=False):
    """
    诊断学生画像是否需要更新state
    """
    raw_message: TriggerUpdateEventMessage
    summary: SummaryStudentReferenceResult
    diagnostic_result: ProfileUpdateDiagnosticResult
    callback_ok: bool
    callback_status_code: int
    callback_error: str

def _summary_node(state: ProfileUpdateWorkflowState) -> ProfileUpdateWorkflowState:
    """
    获取学生画像和上下文总结参考节点
    :param state:
    :return:
    """
    raw = state["raw_message"]
    trigger_type = raw.eventType
    summary = get_student_reference(
        user_id=int(raw.userId),
        trigger_event=trigger_type,
        ref_id=getattr(raw, "refId", None),
        course_id=getattr(raw, "courseId", None),
        occurred_at=raw.occurredAt.isoformat() if raw.occurredAt else None,
    )
    logger.info(f"工作流 summary 节点完成: userId={raw.userId}")
    return {"summary": summary}


def _diagnostic_node(state: ProfileUpdateWorkflowState) -> ProfileUpdateWorkflowState:
    """
    诊断节点
    :param state:
    :return:
    """
    raw = state["raw_message"]
    summary = state["summary"]
    raw_dict = raw.to_dict()
    summary_dict = summary.to_dict()
    raw_dict["summaryReference"] = summary_dict
    result = diagnostic_update_profile(raw_dict)
    logger.info(
        f"工作流 diagnostic 节点完成: userId={result.user_id}, shouldUpdate={result.should_update}"
    )
    return {"diagnostic_result": result}


def _callback_update_node(state: ProfileUpdateWorkflowState) -> ProfileUpdateWorkflowState:
    """
    回调更新学生画像节点
    :param state:
    :return:
    """
    result = state.get("diagnostic_result")
    payload = result.to_dict() if result else {}
    http_result = java_backend_http_client.callback_profile_result(payload)
    logger.info(
        f"工作流 callback_update 节点完成: status={http_result.status_code}, ok={http_result.ok}"
    )
    return {
        "callback_ok": http_result.ok,
        "callback_status_code": http_result.status_code,
        "callback_error": http_result.error,
    }


def build_profile_update_workflow():
    """
    构建学生画像更新工作流
    :return: 学生画像更新工作流
    """
    builder = StateGraph(ProfileUpdateWorkflowState)

    builder.add_node("summary", _summary_node)
    builder.add_node("diagnostic", _diagnostic_node)
    builder.add_node("callback_update", _callback_update_node)

    builder.add_edge(START, "summary")
    builder.add_edge("summary", "diagnostic")
    builder.add_edge("callback_update", END)

    return builder.compile()


profile_update_workflow = build_profile_update_workflow()


def run_profile_update_workflow(message: TriggerUpdateEventMessage) -> ProfileUpdateDiagnosticResult:
    initial_state: ProfileUpdateWorkflowState = {"raw_message": message}
    return profile_update_workflow.invoke(initial_state)["diagnostic_result"]

