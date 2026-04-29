from datetime import datetime
from decimal import Decimal

from sqlalchemy import BigInteger, DateTime, Integer, Numeric, Text
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column


class Base(DeclarativeBase):
    pass


class KbChunk(Base):
    """
    知识库切分块表（RAG 检索主数据）
    对齐 Java: cn.hhh.campusmind.resources.domain.po.KbChunk
    """

    __tablename__ = "kb_chunk"

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True)
    chunk_no: Mapped[str] = mapped_column(Text)
    doc_id: Mapped[int] = mapped_column(BigInteger)
    task_id: Mapped[int] = mapped_column(BigInteger)
    course_id: Mapped[int] = mapped_column(BigInteger)
    chapter_id: Mapped[int] = mapped_column(BigInteger)
    knowledge_point_id: Mapped[int] = mapped_column(BigInteger)
    chunk_index: Mapped[int] = mapped_column(Integer, default=0)
    content: Mapped[str] = mapped_column(Text, default="")
    content_hash: Mapped[str] = mapped_column(Text, default="")
    token_count: Mapped[int] = mapped_column(Integer, default=0)
    char_count: Mapped[int] = mapped_column(Integer, default=0)
    start_offset: Mapped[int] = mapped_column(Integer, default=0)
    end_offset: Mapped[int] = mapped_column(Integer, default=0)
    page_no: Mapped[int] = mapped_column(Integer, default=0)
    modality: Mapped[str] = mapped_column(Text, default="TEXT")
    source_type: Mapped[str] = mapped_column(Text, default="UPLOAD")
    source_ref: Mapped[str] = mapped_column(Text, default="")
    language: Mapped[str] = mapped_column(Text, default="Chinese")
    vector_status: Mapped[str] = mapped_column(Text, default="PENDING")
    quality_score: Mapped[Decimal] = mapped_column(Numeric(5, 2), default=Decimal("0.00"))
    is_deleted: Mapped[int] = mapped_column(Integer, default=0)
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.now)
    updated_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.now)

