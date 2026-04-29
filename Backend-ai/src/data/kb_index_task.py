from datetime import datetime

from sqlalchemy import BigInteger, DateTime, Integer, Text
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column

from src.domain.task_type_enum import TaskTypeEnum
from src.domain.task_status_enum import TaskStatusEnum
from src.domain.task_step_enum import TaskStepEnum


class Base(DeclarativeBase):
    pass


class KbIndexTask(Base):
    """
    知识库文档索引任务表（解析/切分/向量化异步任务）
    对齐 Java: cn.hhh.campusmind.resources.domain.po.KbIndexTask
    """

    __tablename__ = "kb_index_task"

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True)
    task_no: Mapped[str] = mapped_column(Text)
    doc_id: Mapped[int] = mapped_column(BigInteger)
    teacher_id: Mapped[int] = mapped_column(BigInteger)
    course_id: Mapped[int] = mapped_column(BigInteger)
    task_type: Mapped[TaskTypeEnum] = mapped_column(Text, default="DOCUMENT_INDEX")
    status: Mapped[TaskStatusEnum] = mapped_column(Text, default="CREATED")
    current_step: Mapped[TaskStepEnum] = mapped_column(Text, default="INIT")
    retry_count: Mapped[int] = mapped_column(Integer, default=0)
    trace_id: Mapped[str] = mapped_column(Text, default="")
    started_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.now)
    finished_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.now)
    cost_ms: Mapped[int] = mapped_column(BigInteger, default=0)
    result_json: Mapped[str] = mapped_column(Text, default="{}")
    error_code: Mapped[str] = mapped_column(Text, default="")
    error_message: Mapped[str] = mapped_column(Text, default="")
    is_deleted: Mapped[int] = mapped_column(Integer, default=0)
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.now)
    updated_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.now)

