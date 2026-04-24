import json

from langchain.agents import create_agent

from src.agents.profile_update_agent.domain.profile_update_diagnostic_results import ProfileUpdateDiagnosticResult
from src.common.factory import chat_model
from src.common.logger_handler import logger
from src.mq.messages.profile_update_message import TriggerUpdateEventMessage
from src.utils.parse_json import parse_json
from src.utils.prompt_loader import load_prompt

agent = create_agent(
    model=chat_model,
    tools=[],
    system_prompt=load_prompt("profile_update_prompt.txt"),
)


def diagnostic_update_profile(profile_update_msg: TriggerUpdateEventMessage) -> ProfileUpdateDiagnosticResult:
    """
    诊断学生画像是否需要更新
    :param profile_update_msg: 学生画像更新消息
    :return: 学生画像更新诊断结果
    """
    prompt_input = profile_update_msg
    if not isinstance(prompt_input, str):
        prompt_input = json.dumps(prompt_input, ensure_ascii=False)

    result = agent.invoke({"messages": [{"role": "user", "content": prompt_input}]})
    messages = result.get("messages") or []

    logger.info(f"诊断是否需要更新画像结果: {result}")

    content = ""
    for m in reversed(messages):
        c = getattr(m, "content", None)
        if isinstance(c, str) and c.strip():
            content = c.strip()
            break
    parsed: dict = parse_json(content)


    if not isinstance(parsed, dict) or not parsed:
        parsed = {
            "userId": profile_update_msg.get("userId"),
            "profileVersion": profile_update_msg.get("profileVersion"),
            "eventType": profile_update_msg.get("eventType") or "manual",
            "courseId": profile_update_msg.get("courseId"),
            "shouldUpdate": False,
            "reason": "invalid_model_output",
            "delta": {},
            "evidence": [],
            "confidence": {"overall": 0.0},
        }

    return ProfileUpdateDiagnosticResult(
        user_id=parsed.get("userId"),
        profile_version=parsed.get("profileVersion"),
        event_type=parsed.get("eventType"),
        should_update=bool(parsed.get("shouldUpdate", False)),
        reason=str(parsed.get("reason", "")),
        delta=parsed.get("delta") if isinstance(parsed.get("delta"), dict) else {},
        evidence=parsed.get("evidence") if isinstance(parsed.get("evidence"), list) else [],
        confidence=parsed.get("confidence") if isinstance(parsed.get("confidence"), dict) else {},
    )





