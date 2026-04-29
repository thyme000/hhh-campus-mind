import os
import re
import uuid
from datetime import datetime
from urllib.parse import urlparse

from dotenv import load_dotenv

from src.common.logger_handler import logger

load_dotenv()


class AliOssUtil:
    """OSS 工具：只按当前 .env 变量读取。"""

    def __init__(self):
        self.endpoint = (os.getenv("ALIOSS_ENDPOINT")).strip()
        self.bucket_name = (os.getenv("ALIOSS_BUCKET_NAME")).strip()
        self.access_key_id = (os.getenv("ALIOSS_ACCESS_KEY_ID")).strip()
        self.access_key_secret = (os.getenv("ALIOSS_ACCESS_KEY_SECRET")).strip()

    def upload_for_object_key(self, content: bytes, original_filename: str,
                              user_id: int | None = None, biz_prefix: str = "upload") -> str:
        """
        上传文件到OSS，返回object_key。=
        :param content: 文件内容
        :param original_filename: 原始文件名
        :param user_id: 用户id
        :param biz_prefix: 业务前缀
        :return: object_key
        """
        object_key = self.build_unique_object_key(user_id, biz_prefix, original_filename)
        self.put_object(content, object_key)
        return object_key

    def put_object(self, content: bytes, object_key: str) -> None:
        """
        将字节内容上传到OSS
        :param content: 要上传的字节内容
        :param object_key: OSS对象键
        :return: None
        """
        bucket = self._build_bucket()
        bucket.put_object(object_key, content)
        logger.info("OSS upload success, objectKey=%s", object_key)

    def get_object_bytes(self, key_or_url: str) -> bytes:
        """
        从OSS获取文件内容。
        :param key_or_url: 文件key或url
        :return: 文件内容
        """
        object_key = self.normalize_to_object_key(key_or_url)
        bucket = self._build_bucket()
        return bucket.get_object(object_key).read()

    def head_object(self, key_or_url: str) -> dict:
        """
        获取从OSS获取文件元数据。
        :param key_or_url: 文件key或url
        :return: 文件元数据
        """
        object_key = self.normalize_to_object_key(key_or_url)
        bucket = self._build_bucket()
        meta = bucket.head_object(object_key)
        return {
            "content_length": getattr(meta, "content_length", None),
            "content_type": getattr(meta, "content_type", None),
            "etag": getattr(meta, "etag", None),
            "last_modified": getattr(meta, "last_modified", None),
        }

    def sign_get_url(self, key_or_url: str, expire_seconds: int = 300) -> str:
        """
        获得签名url
        :param key_or_url:
        :param expire_seconds:
        :return:
        """
        object_key = self.normalize_to_object_key(key_or_url)
        bucket = self._build_bucket()
        return bucket.sign_url("GET", object_key, max(1, int(expire_seconds)))

    def normalize_to_object_key(self, key_or_url: str) -> str:
        """
        标准化object_key。
        :param key_or_url: 文件key或url
        :return: object_key
        """
        value = (key_or_url or "").strip()
        if not value or not value.startswith(("http://", "https://")):
            return value
        path = urlparse(value).path or ""
        return path[1:] if path.startswith("/") else path

    def build_unique_object_key(self, user_id: int | None, biz_prefix: str, original_filename: str) -> str:
        """
        构建唯一object_key。
        :param user_id: 用户id
        :param biz_prefix: 业务前缀
        :param original_filename: 原始文件名
        :return: object_key
        """
        biz = re.sub(r"[^a-zA-Z0-9_-]", "_", (biz_prefix or "upload"))
        uid = str(user_id) if user_id is not None else "anonymous"
        date_folder = datetime.now().strftime("%Y%m%d")
        safe_name = self._sanitize_filename(original_filename)
        return f"uploads/{biz}/{uid}/{date_folder}/{uuid.uuid4().hex}_{safe_name}"

    @staticmethod
    def _sanitize_filename(name: str | None) -> str:
        """
        清理和标准化文件名
        :param name: 原始文件名
        :return: 标准化后的安全文件名
        """
        if not name:
            return "file"
        base = name.strip().replace("\\", "/").split("/")[-1]
        base = re.sub(r"[^a-zA-Z0-9._-]", "_", base)
        return base[:120] if base else "file"

    def _build_bucket(self):
        """
        构建OSS bucket。
        :return:
        """
        if not (self.endpoint and self.bucket_name and self.access_key_id and self.access_key_secret):
            raise RuntimeError("AliOSS环境变量缺失: ALIOSS_ENDPOINT/ALIOSS_BUCKET_NAME/ALIOSS_ACCESS_KEY_ID/ALIOSS_ACCESS_KEY_SECRET")
        import oss2

        endpoint = self.endpoint if self.endpoint.startswith(("http://", "https://")) else f"https://{self.endpoint}"
        auth = oss2.Auth(self.access_key_id, self.access_key_secret)
        return oss2.Bucket(auth, endpoint, self.bucket_name)
