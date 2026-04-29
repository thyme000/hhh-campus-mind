from typing import Any
from langchain.tools import tool

import redis

from src.config.config import redis_config

r = redis.Redis(
    host=redis_config["REDIS_HOST"],
    port=int(redis_config["REDIS_PORT"]),
    db=int(redis_config["REDIS_DB"]),
    password=redis_config["REDIS_PASSWORD"],
    decode_responses=True,
)


def get_user_context(user_id: int, session_id: int) -> str:
    """获取用户会话上下文(获取最新的10条消息)"""

    key = f"{redis_config['USER_SESSION_CONTEXT']}{user_id}:{session_id}"
    msg = r.zrevrange(key, 0, 10)
    items: list[dict[str, Any]] = []
    for s in reversed(msg):  # zrevrange 是新->旧，翻转成旧->新
        s = (s or "").strip()
        if not s:
            continue

        if s.startswith("human:"):
            items.append({"role": "human", "content": s[len("human:"):].lstrip()})
        elif s.startswith("assistant:"):
            items.append({"role": "assistant", "content": s[len("assistant:"):].lstrip()})
        else:
            items.append({"role": "unknown", "content": s})

    return "\n".join(f"{x['role']}: {x['content']}" for x in items)



