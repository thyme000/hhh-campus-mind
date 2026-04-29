from enum import Enum
from typing import Any


class FileExtEnum(Enum):
    """文件后缀枚举（与 Java 端 FileExtEnum 对齐）"""

    PDF = "pdf"
    DOCX = "docx"
    PPT = "ppt"
    PPTX = "pptx"
    MD = "md"
    MARKDOWN = "markdown"
    TXT = "txt"

    @classmethod
    def from_any(cls, raw: Any) -> "FileExtEnum":
        """
        兼容多种输入：
        - Java 枚举名：PDF / DOCX / ...
        - 后缀码：pdf / docx / ...
        - 对象形式：{"code":"pdf"} / {"name":"PDF"} / {"value":"pdf"}
        """
        if isinstance(raw, cls):
            return raw
        if raw is None:
            raise ValueError("fileExt is required")

        if isinstance(raw, dict):
            for key in ("code", "name", "value"):
                if raw.get(key) is not None:
                    raw = raw.get(key)
                    break

        text = str(raw).strip()
        if not text:
            raise ValueError("fileExt is blank")

        # 先按枚举名匹配
        upper = text.upper()
        if upper in cls.__members__:
            return cls[upper]

        # 再按 code 匹配
        lower = text.lower()
        for item in cls:
            if item.value == lower:
                return item

        raise ValueError(f"Unsupported fileExt: {raw}")
