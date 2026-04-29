from __future__ import annotations

import base64
import hashlib
import hmac
import json
from dataclasses import dataclass
from datetime import datetime
from time import mktime
from typing import List, Sequence
from urllib.parse import urlencode
from wsgiref.handlers import format_date_time

import numpy as np
import requests
from langchain_core.embeddings import Embeddings


class XfyunEmbeddingError(RuntimeError):
    pass


@dataclass(frozen=True)
class _ParsedUrl:
    host: str
    path: str
    schema: str


def _parse_url(request_url: str) -> _ParsedUrl:
    stidx = request_url.index("://")
    host = request_url[stidx + 3 :]
    schema = request_url[: stidx + 3]
    edidx = host.index("/")
    if edidx <= 0:
        raise XfyunEmbeddingError(f"非法的URL: {request_url}")
    path = host[edidx:]
    host = host[:edidx]
    return _ParsedUrl(host=host, path=path, schema=schema)


def _assemble_auth_url(request_url: str, method: str, api_key: str, api_secret: str) -> str:
    u = _parse_url(request_url)
    date = format_date_time(mktime(datetime.now().timetuple()))

    signature_origin = f"host: {u.host}\ndate: {date}\n{method} {u.path} HTTP/1.1"
    signature_sha = hmac.new(
        api_secret.encode("utf-8"),
        signature_origin.encode("utf-8"),
        digestmod=hashlib.sha256,
    ).digest()
    signature_b64 = base64.b64encode(signature_sha).decode("utf-8")

    authorization_origin = (
        f'api_key="{api_key}", algorithm="hmac-sha256", '
        f'headers="host date request-line", signature="{signature_b64}"'
    )
    authorization = base64.b64encode(authorization_origin.encode("utf-8")).decode("utf-8")

    params = {"host": u.host, "date": date, "authorization": authorization}
    return request_url + "?" + urlencode(params)


def _build_body(app_id: str, payload_obj: dict, domain: str) -> dict:
    payload_b64 = base64.b64encode(json.dumps(payload_obj, ensure_ascii=False).encode("utf-8")).decode("utf-8")
    return {
        "header": {"app_id": app_id, "status": 3},
        "parameter": {"emb": {"domain": domain, "feature": {"encoding": "utf8"}}},
        "payload": {"messages": {"text": payload_b64}},
    }


def _parse_vector(resp_json: dict) -> List[float]:
    code = ((resp_json.get("header") or {}).get("code")) or 0
    if code != 0:
        raise XfyunEmbeddingError(f"xfyun embedding error code={code} resp={resp_json}")

    text_base = (((resp_json.get("payload") or {}).get("feature") or {}).get("text")) or ""
    if not text_base:
        raise XfyunEmbeddingError(f"xfyun embedding missing payload.feature.text resp={resp_json}")

    raw = base64.b64decode(text_base)
    vec = np.frombuffer(raw, dtype=np.dtype(np.float32).newbyteorder("<"))
    return vec.astype(float).tolist()


class XfyunEmbeddings(Embeddings):
    """
    LangChain Embeddings 适配器：对接讯飞 Embedding HTTP 服务
    - embed_query: domain=query
    - embed_documents: domain=para
    """

    def __init__(
        self,
        app_id: str,
        embedding_key: str,
        api_secret: str,
        endpoint: str = "https://emb-cn-huabei-1.xf-yun.com/",
        timeout_seconds: int = 20,
    ) -> None:
        self.app_id = app_id
        self.embedding_key = embedding_key
        self.api_secret = api_secret
        self.endpoint = endpoint
        self.timeout_seconds = timeout_seconds
        self.session = requests.Session()
        self.session.trust_env = False

    def _embed_one(self, text: str, domain: str) -> List[float]:
        url = _assemble_auth_url(
            request_url=self.endpoint,
            method="POST",
            api_key=self.embedding_key,
            api_secret=self.api_secret,
        )
        payload_obj = {"messages": [{"role": "user", "content": text}]}
        body = _build_body(app_id=self.app_id, payload_obj=payload_obj, domain=domain)

        resp = self.session.post(url, json=body, headers={"content-type": "application/json"}, timeout=self.timeout_seconds)
        resp.raise_for_status()
        return _parse_vector(resp.json())

    def embed_query(self, text: str) -> List[float]:
        """
        用户输入向量化
        :param text:
        :return:
        """
        return self._embed_one(text=text, domain="query")

    def embed_documents(self, texts: Sequence[str]) -> List[List[float]]:
        """
        文档向量化
        :param texts:
        :return:
        """
        return [self._embed_one(text=t, domain="para") for t in texts]