from datetime import datetime

from sqlalchemy import BigInteger, DateTime, Integer, Text
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column


class Base(DeclarativeBase):
    pass


class KbChunkEmbedding(Base):
    """
    知识库切分块向量表（pgvector）
    对齐 Java: cn.hhh.campusmind.resources.domain.po.KbChunkEmbedding
    """

    __tablename__ = "kb_chunk_embedding"

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True)
    chunk_id: Mapped[int] = mapped_column(BigInteger)
    doc_id: Mapped[int] = mapped_column(BigInteger)
    course_id: Mapped[int] = mapped_column(BigInteger)
    embedding_dim: Mapped[int] = mapped_column(Integer, default=0)
    # PostgreSQL vector 类型在 Python 侧先用 Text 承载字面量，如 "[0.1,0.2,...]"
    embedding: Mapped[str] = mapped_column(Text, default="")
    is_deleted: Mapped[int] = mapped_column(Integer, default=0)
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.now)
    updated_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.now)

