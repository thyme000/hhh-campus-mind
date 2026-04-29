import os
import tempfile
from pathlib import Path

from src.common.logger_handler import logger
from src.common.utils.ali_util import AliOssUtil


class TempDocumentLoader:
    """将 OSS 文件下载到本地临时目录，并提供删除能力。"""

    _ali_util = AliOssUtil()

    @staticmethod
    def download_from_oss_to_temp(object_key: str) -> str:
        """
        按 object_key 从 OSS 下载文件到 temp 目录，返回本地绝对路径。
        :param object_key: OSS object key（也兼容 URL，由 AliOssUtil 统一处理）
        :return: 本地临时文件绝对路径
        """
        key = (object_key or "").strip()
        if not key:
            raise ValueError("object_key 不能为空")

        content = TempDocumentLoader._ali_util.get_object_bytes(key)
        suffix = Path(TempDocumentLoader._ali_util.normalize_to_object_key(key)).suffix
        temp_dir = os.path.join(os.path.dirname(__file__), "..", ".." , "..", "temp")

        fd, temp_path = tempfile.mkstemp(prefix="kb_doc_", suffix=suffix, dir=temp_dir)
        try:
            with os.fdopen(fd, "wb") as f:
                f.write(content)
        except Exception:
            try:
                os.close(fd)
            except Exception:
                pass
            if os.path.exists(temp_path):
                os.remove(temp_path)
            raise

        logger.info("文件已保存在临时目录, object_key=%s, temp_path=%s", key, temp_path)
        return temp_path

    @staticmethod
    def delete_temp_file(file_path: str) -> bool:
        """
        删除本地临时文件。
        :param file_path: 本地文件路径
        :return: 是否删除成功（不存在也返回 True）
        """
        path = (file_path or "").strip()
        if not path:
            return True
        if not os.path.exists(path):
            return True
        if os.path.isdir(path):
            raise ValueError(f"delete_temp_file 仅支持文件路径，当前为目录: {path}")

        os.remove(path)
        logger.info("删除临时文件成功, path=%s", path)
        return True
