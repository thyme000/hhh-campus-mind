from enum import Enum
from typing import Any


class MimeTypeEnum(Enum):
    """MIME 类型枚举（与 Java 端 MimeTypeEnum 对齐）"""

    TXT = "text/plain"
    MARKDOWN = "text/markdown"

    PDF = "application/pdf"
    DOC = "application/msword"
    DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    PPT = "application/vnd.ms-powerpoint"
    PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation"

    PNG = "image/png"
    JPG = "image/jpeg"
    JPEG = "image/jpeg"

    MP3 = "audio/mpeg"
    MP4 = "video/mp4"

    JSON = "application/json"
    OCTET_STREAM = "application/octet-stream"

    @classmethod
    def from_any(cls, raw: Any) -> "MimeTypeEnum":
        """
        兼容多种输入：
        - Java 枚举名：PDF / DOCX / ...
        - MIME 字符串：application/pdf / image/png / ...
        - 对象形式：{"value":"application/pdf"} / {"name":"PDF"}
        """
        if isinstance(raw, cls):
            return raw
        if raw is None:
            raise ValueError("mimeType is required")

        if isinstance(raw, dict):
            for key in ("value", "name", "code"):
                if raw.get(key) is not None:
                    raw = raw.get(key)
                    break

        text = str(raw).strip()
        if not text:
            raise ValueError("mimeType is blank")

        # 先按枚举名匹配
        upper = text.upper()
        if upper in cls.__members__:
            return cls[upper]

        # 再按 MIME value 匹配
        lower = text.lower()
        for item in cls:
            if item.value == lower:
                return item

        raise ValueError(f"Unsupported mimeType: {raw}")
