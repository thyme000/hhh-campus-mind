import json


def parse_json(content: str) -> dict:
    """
    解析 json 字符串（容错：从文本中截取 {...}）。
    始终返回 dict，解析失败返回 {}。
    """
    parsed: object = {}

    if content:
        try:
            parsed = json.loads(content)
        except Exception:
            try:
                start = content.find("{")
                end = content.rfind("}")
                if start != -1 and end != -1 and end > start:
                    parsed = json.loads(content[start : end + 1])
            except Exception:
                parsed = {}

    return parsed if isinstance(parsed, dict) else {}