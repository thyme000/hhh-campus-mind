import json

from dataclasses import dataclass
from typing import Any, Dict, Optional

import requests

from src.config.config import backend_config


@dataclass
class HttpResult:
    """统一 HTTP 返回结构。"""

    ok: bool
    status_code: int
    data: Optional[Any] = None
    text: str = ""
    error: str = ""


class JavaBackendHttpClient:
    """
    与 Java 后端通信的通用 HTTP 客户端。

    支持：
    - 通用 request/get/post/put/delete
    - 自动拼接 backend.yml 的 BASE URL
    - 自动携带 Internal-Token（若配置存在）
    - AI 场景便捷方法（获取上下文、回调画像结果）
    """

    def __init__(
        self,
        base_url: Optional[str] = None,
        internal_token: Optional[str] = None,
        timeout_seconds: Optional[int] = None,
    ) -> None:
        self.base_url = (base_url or backend_config["BACKEND_BASE_URL"]).rstrip("/")
        self.internal_api_token = internal_token if internal_token is not None else backend_config["INTERNAL_API_TOKEN"]
        self.timeout_seconds = int(timeout_seconds or backend_config["REQUEST_TIMEOUT_SECONDS"])

        self.session = requests.Session()
        # 本地服务通信时避免被系统代理影响
        self.session.trust_env = False

    def _build_url(self, path: str) -> str:
        if path.startswith("http://") or path.startswith("https://"):
            return path
        if not path.startswith("/"):
            path = "/" + path
        return f"{self.base_url}{path}"

    def _build_headers(self, extra_headers: Optional[Dict[str, str]] = None) -> Dict[str, str]:
        headers = {"Content-Type": "application/json"}
        if self.internal_api_token:
            headers["Internal-API-Token"] = self.internal_api_token
        if extra_headers:
            headers.update(extra_headers)
        return headers

    def request(
        self,
        method: str,
        path: str,
        *,
        params: Optional[Dict[str, Any]] = None,
        json_body: Optional[Dict[str, Any]] = None,
        data: Optional[str] = None,
        headers: Optional[Dict[str, str]] = None,
        timeout_seconds: Optional[int] = None,
    ) -> HttpResult:
        url = self._build_url(path)
        req_timeout = int(timeout_seconds or self.timeout_seconds)
        req_headers = self._build_headers(headers)

        try:
            resp = self.session.request(
                method=method.upper(),
                url=url,
                params=params,
                json=json_body,
                data=data,
                headers=req_headers,
                timeout=req_timeout,
            )
            text = resp.text or ""

            try:
                parsed = resp.json()
            except Exception:
                parsed = None

            return HttpResult(
                ok=200 <= resp.status_code < 300,
                status_code=resp.status_code,
                data=parsed,
                text=text,
                error="" if 200 <= resp.status_code < 300 else f"HTTP {resp.status_code}",
            )
        except Exception as ex:
            return HttpResult(
                ok=False,
                status_code=0,
                data=None,
                text="",
                error=str(ex),
            )

    def get(
        self,
        path: str,
        *,
        params: Optional[Dict[str, Any]] = None,
        headers: Optional[Dict[str, str]] = None,
        timeout_seconds: Optional[int] = None,
    ) -> HttpResult:
        return self.request(
            method="GET",
            path=path,
            params=params,
            headers=headers,
            timeout_seconds=timeout_seconds,
        )

    def post(
        self,
        path: str,
        *,
        json_body: Optional[Dict[str, Any]] = None,
        data: Optional[str] = None,
        headers: Optional[Dict[str, str]] = None,
        timeout_seconds: Optional[int] = None,
    ) -> HttpResult:
        return self.request(
            method="POST",
            path=path,
            json_body=json_body,
            data=data,
            headers=headers,
            timeout_seconds=timeout_seconds,
        )

    def put(
        self,
        path: str,
        *,
        json_body: Optional[Dict[str, Any]] = None,
        data: Optional[str] = None,
        headers: Optional[Dict[str, str]] = None,
        timeout_seconds: Optional[int] = None,
    ) -> HttpResult:
        return self.request(
            method="PUT",
            path=path,
            json_body=json_body,
            data=data,
            headers=headers,
            timeout_seconds=timeout_seconds,
        )

    def delete(
        self,
        path: str,
        *,
        params: Optional[Dict[str, Any]] = None,
        headers: Optional[Dict[str, str]] = None,
        timeout_seconds: Optional[int] = None,
    ) -> HttpResult:
        return self.request(
            method="DELETE",
            path=path,
            params=params,
            headers=headers,
            timeout_seconds=timeout_seconds,
        )

    def callback_profile_result(self, result_payload: Dict[str, Any]) -> HttpResult:
        """
        回调 AI 画像更新结果。
        """
        path = backend_config["PROFILE_CALLBACK_API_PATH"]
        return self.post(
            path=path,
            data=json.dumps(result_payload, ensure_ascii=False),
            headers={"Content-Type": "application/json"},
        )

    def callback_generate_session_title_result(self, result_payload: Dict[str, Any]) -> HttpResult:
        """
        回调 AI 会话标题生成结果。
        """
        path = backend_config["GENERATE_SESSION_TITLE_CALLBACK_API_PATH"]
        return self.post(
            path=path,
            data=json.dumps(result_payload, ensure_ascii=False),
            headers={"Content-Type": "application/json"},
        )

java_backend_http_client = JavaBackendHttpClient()

