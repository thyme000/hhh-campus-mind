from typing import TypedDict

from langgraph.constants import START, END
from langgraph.graph import StateGraph

from src.agents.summary_agent.summary_agent import generate_session_title
from src.common.logger_handler import logger
from src.domain.session_title_result import SessionTitleResult
from src.mq.messages.generate_session_title_message import GenerateSessionTitleMessage
from src.utils.redis_util import get_user_context


class GenerateSessionTitleWorkflowState(TypedDict, total=False):
    """
    生成会话标题state
    """
    user_id: int
    session_id: int
    context_text: str
    title: str


def _summary_node(state: GenerateSessionTitleWorkflowState) -> GenerateSessionTitleWorkflowState:
    """
    获取上下文总结节点
    :param state:
    :return:
    """
    session_id = state["session_id"]
    user_id = state["user_id"]
    context_text = state["context_text"]

    title = generate_session_title(
        user_id=user_id,
        session_id=session_id,
        context_text=context_text,
    )
    logger.info(f"工作流 summary 节点完成: sessionId={session_id}, title={title}")
    return {"title": title}

def _generate_session_title_node(state: GenerateSessionTitleWorkflowState) -> GenerateSessionTitleWorkflowState:
    """
    获取用户上下文节点
    :param state:
    :return:
    """
    # 获取上下文
    user_id = state["user_id"]
    session_id = state["session_id"]
    context_text = get_user_context(user_id, session_id)
    if not context_text:
        return {"context_text": ""}
    logger.info(f"工作流 generate_session_title 节点完成: sessionId={session_id}, context_text={context_text}")
    return {"context_text": context_text}


def build_generate_session_title_workflow():
    """
    构建生成会话标题工作流
    :return: 生成会话标题工作流
    """
    builder = StateGraph(GenerateSessionTitleWorkflowState)

    builder.add_node("generate_session_title_node", _generate_session_title_node)
    builder.add_node("summary_node", _summary_node)

    builder.add_edge(START, "generate_session_title_node")
    builder.add_edge("generate_session_title_node", "summary_node")
    builder.add_edge("summary_node", END)

    return builder.compile()

generate_session_title_workflow = build_generate_session_title_workflow()


def run_generate_session_title_workflow(message: GenerateSessionTitleMessage) -> SessionTitleResult:
    initial_state: GenerateSessionTitleWorkflowState = {"user_id": message.userId, "session_id": message.sessionId}
    result = generate_session_title_workflow.invoke(initial_state)
    return SessionTitleResult(
        sessionId=message.sessionId,
        title=result["title"]
    )
