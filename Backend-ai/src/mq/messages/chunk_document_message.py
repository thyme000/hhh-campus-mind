from dataclasses import dataclass
from typing import Any, Optional

from src.domain.FileExtEnum import FileExtEnum
from src.domain.MimeTypeEnum import MimeTypeEnum


@dataclass
class ChunkDocumentMessage:
    """文档解析/切分消息（与 Java 端 DocumentForChunkMessage 对齐）"""

    # 文档主键（kb_document.id）
    documentId: int
    # 业务文档号（幂等/对账）
    docNo: str
    # 上传教师
    teacherId: int
    # 课程ID（检索隔离）
    courseId: int
    # OSS object key
    storageKey: str
    # 原始文件名
    originalFilename: str
    # 文件后缀（与 Java 端 FileExtEnum 对齐）
    fileExt: FileExtEnum
    # MIME（与 Java 端 MimeTypeEnum 对齐）
    mimeType: MimeTypeEnum
    # 文件大小（字节）
    fileSizeBytes: int
    # 内容摘要（可空）
    fileSha256: Optional[str] = None
    # 索引任务号（回写任务状态）
    indexTaskNo: Optional[str] = None
    # 链路追踪ID（可空）
    traceId: Optional[str] = None
    # 任务ID（回写任务状态）
    taskId: int = None

    def to_dict(self) -> dict:
        """转换为字典，用于 JSON 序列化"""
        return {
            "documentId": self.documentId,
            "docNo": self.docNo,
            "teacherId": self.teacherId,
            "courseId": self.courseId,
            "storageKey": self.storageKey,
            "originalFilename": self.originalFilename,
            "fileExt": self.fileExt.name,
            "mimeType": self.mimeType.name,
            "fileSizeBytes": self.fileSizeBytes,
            "fileSha256": self.fileSha256,
            "indexTaskNo": self.indexTaskNo,
            "traceId": self.traceId,
            "taskId": self.taskId
        }

    @classmethod
    def from_dict(cls, data: dict) -> "ChunkDocumentMessage":
        """
        从字典创建实例。
        兼容 Java 枚举在 MQ 中可能出现的两种形态：
        1) "PDF"/"DOCX" 这类字符串；
        2) {"value":"application/pdf"} 这类对象。
        """
        return cls(
            documentId=int(data["documentId"]),
            docNo=str(data["docNo"]),
            teacherId=int(data["teacherId"]),
            courseId=int(data["courseId"]),
            storageKey=str(data["storageKey"]),
            originalFilename=str(data["originalFilename"]),
            fileExt=FileExtEnum.from_any(data.get("fileExt")),
            mimeType=MimeTypeEnum.from_any(data.get("mimeType")),
            fileSizeBytes=int(data.get("fileSizeBytes") or 0),
            fileSha256=cls._to_optional_str(data.get("fileSha256")),
            indexTaskNo=cls._to_optional_str(data.get("indexTaskNo")),
            traceId=cls._to_optional_str(data.get("traceId")),
            taskId=int(data.get("taskId")),
        )

    @staticmethod
    def _to_optional_str(value: Any) -> Optional[str]:
        if value is None:
            return None
        text = str(value).strip()
        return text if text else None
