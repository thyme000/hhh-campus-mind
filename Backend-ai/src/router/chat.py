import json
from typing import AsyncGenerator, Optional

from fastapi import Header, HTTPException
from fastapi.responses import StreamingResponse
from langchain_core.messages import AIMessage, AIMessageChunk
from pydantic import BaseModel

from src.agents.chat_agent.chat_agent import chat_agent
from src.common.logger_handler import logger
from src.config.config import backend_config
from fastapi import APIRouter


chat_router = APIRouter(prefix="/internal/chat", tags=["chat"])



class ChatRequest(BaseModel):
    sessionId: str
    content: str
    userId: int
    traceId: Optional[str] = ""


class ChatResponse(BaseModel):
    content: str
    traceId: str = ""
    # TODO暂时只支持文本内容
    contentType: str = "text"


def _check_internal_token(internal_api_token: Optional[str]) -> None:
    """
    校验token是否正确
    :param internal_api_token:
    :return:
    """
    expected = (backend_config.get("INTERNAL_API_TOKEN") or "").strip()
    if not expected:
        return
    token = (internal_api_token or "").strip()
    if token != expected:
        raise HTTPException(status_code=401, detail="非法的Internal-API-Token")


def _extract_chunk_text(chunk: object) -> str:
    """仅解析流式 chunk 的文本内容"""
    content = getattr(chunk, "content", "")
    return content if isinstance(content, str) else ""


@chat_router.post("/stream")
@chat_router.post("/stream")
def chat_stream(req: ChatRequest, internal_api_token: Optional[str] = Header(default=None, alias="Internal-API-Token")):
    _check_internal_token(internal_api_token)

    async def event_generator() -> AsyncGenerator[str, None]:
        prompt = (req.content or "").strip()
        if not prompt:
            yield 'event: error\ndata: {"message":"请输入消息内容"}\n\n'
            yield "event: done\ndata: \n\n"
            return

        yield f'event: meta\ndata: {json.dumps({"traceId": req.traceId or "", "sessionId": req.sessionId}, ensure_ascii=False)}\n\n'

        try:
            for mode, payload in chat_agent.stream(
                {"messages": [{"role": "user", "content": prompt}]},
                stream_mode=["messages"],   # 关键：只拿消息流，不拿 values 快照
            ):
                if mode != "messages":
                    continue

                msg = payload[0] if isinstance(payload, tuple) else payload
                if isinstance(msg, (AIMessage, AIMessageChunk)):
                    text = getattr(msg, "content", "")
                    if isinstance(text, str) and text:
                        yield f"event: message\ndata: {text}\n\n"

            yield "event: done\ndata: \n\n"
        except Exception as ex:
            yield f'event: error\ndata: {json.dumps({"message": str(ex)}, ensure_ascii=False)}\n\n'
            yield "event: done\ndata: \n\n"

    return StreamingResponse(event_generator(), media_type="text/event-stream")

