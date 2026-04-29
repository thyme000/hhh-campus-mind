import asyncio
import datetime
import json
import os
import re
from typing import List, Optional

from langchain_core.documents import Document
import aio_pika
from aio_pika import IncomingMessage, Exchange, DeliveryMode
from dotenv import load_dotenv
from langchain_community.document_loaders import PyMuPDFLoader, TextLoader
from sqlalchemy import create_engine
from functools import partial
from src.common.factory import embed_model
from src.common.logger_handler import logger
from src.common.utils.ali_util import AliOssUtil
from src.common.utils.load_document_in_temp import TempDocumentLoader
from src.config.config import rabbitmq_config
from src.domain.FileExtEnum import FileExtEnum
from src.domain.MimeTypeEnum import MimeTypeEnum
from src.domain.task_status_enum import TaskStatusEnum
from src.domain.task_step_enum import TaskStepEnum
from src.mq.messages.chunk_document_message import ChunkDocumentMessage
from src.rag.splitters.pdf_splitter import AsyncPdfSplitter
from src.rag.splitters.txt_splitter import AsyncTextSplitter

load_dotenv()
ali = AliOssUtil()

async def on_message(message: IncomingMessage, exchange: Exchange = None):
    """
    处理消息
    :param message: 消息对象
    :return:
    """
    try:
        body_text = message.body.decode("utf-8")
        payload = json.loads(body_text)
        await process_message_logic(payload)
    except Exception as e:
        # 获取第一次失败的时间
        first_failed_at = (message.headers or {}).get("x-first-failed-at")
        retry_count = int((message.headers or {}).get("x-retry-count", 0))
        if retry_count < 2:
            logger.error(f"消息 {payload} 重试中,错误信息: {str(e)}")
            # 重试次数小于2次,进入重试队列
            new_headers = {
                "x-retry-count": retry_count + 1,
                "x-first-failed-at": first_failed_at or datetime.datetime.now().isoformat(),
            }
            await exchange.publish(
                aio_pika.Message(
                    body=message.body,
                    headers=new_headers,
                    delivery_mode=DeliveryMode.PERSISTENT,
                    content_type="application/json",
                ),
                routing_key=rabbitmq_config["KB_DOCUMENT_PARSE_RETRY_ROUTING_KEY"],
            )
        else:
            # 重试超过两次,进入死信队列
            logger.error(f"消息 {payload} 重试超过两次,进入死信队列,错误信息: {str(e)}")
            new_headers = {
                "x-retry-count": retry_count,
                "x-first-failed-at": first_failed_at or datetime.datetime.now().isoformat(),
                "x-error": str(e)[:500],
            }
            await exchange.publish(
                aio_pika.Message(
                    body=message.body,
                    headers=new_headers,
                    delivery_mode=DeliveryMode.PERSISTENT,
                    content_type="application/json",
                ),
                routing_key=rabbitmq_config["KB_DOCUMENT_PARSE_DLQ_ROUTING_KEY"],
            )
            update_task_status(payload.get("taskId"), TaskStatusEnum.FAILED,
                                payload.get("traceId"), TaskStepEnum.UPSERT, 0,
                                int(datetime.datetime.now().timestamp() * 1000 - int(first_failed_at)),
                                result_json=json.dumps({"message": "重试超过两次,进入死信队列", "error": str(e)}))
    finally:
        await message.ack()


# 异步处理函数（真正的业务逻辑）
async def process_message_logic(payload: dict):
    """
    上传资源切片,向量化
    Args: payload: 消息内容
    """
    file_path = None
    if payload:
        logger.info(f"开始处理消息: {payload}")

        chunk_documents: List[Document] = []
        msg = ChunkDocumentMessage.from_dict(payload)

        ms = datetime.datetime.now().timestamp() * 1000
        # 更新任务状态为解析中
        update_task_status(msg.taskId, TaskStatusEnum.RUNNING,
                           msg.traceId, TaskStepEnum.INIT, 0)
        try:
            # 获取源文件
            file_path = TempDocumentLoader.download_from_oss_to_temp(msg.storageKey)

            if msg.fileExt == FileExtEnum.PDF:
                # pdf TODO -> 或许后面要特别处理一下pdf中存在的图片等
                file_content = PyMuPDFLoader(file_path).load()
                pdf_splitter = AsyncPdfSplitter()
                chunk_documents = await pdf_splitter.split_documents(file_content)
            elif msg.fileExt == FileExtEnum.TXT:
                # txt
                file_content = TextLoader(file_path, encoding='utf-8').load()
                text_splitter = AsyncTextSplitter()
                chunk_documents = await text_splitter.split_documents(file_content)
            elif msg.fileExt in (FileExtEnum.PPT, FileExtEnum.DOCX,
                                 FileExtEnum.MD, FileExtEnum.MARKDOWN):
                logger.warning(f"暂不支持的文件类型: {msg.fileExt}")
            else:
                logger.error(f"不支持的文件类型")

            # 解析chunkDocuments
            chunks = [doc.page_content for doc in chunk_documents]
            chunk_source_refs = [doc.metadata.get("source_ref") for doc in chunk_documents]

            if chunks:
                logger.info(f"开始向量化 {len(chunks)} 个切片")
                vectors = vectorization(chunks)
                chunk_ids = save_chunks(
                    chunks, msg.documentId, msg.taskId, msg.mimeType, msg.courseId,
                    source_refs=chunk_source_refs,
                )
                save_vectors(vectors, chunk_ids, msg.documentId, msg.courseId)
                update_task_status(msg.taskId, TaskStatusEnum.SUCCESS,
                                   msg.traceId, TaskStepEnum.DONE, 0,
                                   int(datetime.datetime.now().timestamp() * 1000 - ms),
                                   result_json=json.dumps({"message": "解析成功", "chunk_count": len(chunk_ids)}))
            else:
                logger.error(f"解析文档失败, 为空")
                update_task_status(msg.taskId, TaskStatusEnum.FAILED,
                                   msg.traceId, TaskStepEnum.UPSERT, 0,
                                   int(datetime.datetime.now().timestamp() * 1000 - ms),
                                   error_code="1000", error_message="解析文档失败, 为空")
        except Exception as e:
            logger.error(f"处理文件失败: {file_path}, 错误: {str(e)}", exc_info=True)
            TempDocumentLoader.delete_temp_file(file_path)
            raise

async def main():
    """启动异步消费者(pika不支持异步消费)"""
    # 建立连接
    connection = await aio_pika.connect_robust(
        host=os.getenv("RABBITMQ_HOST"),
        port=int(os.getenv("RABBITMQ_PORT")),
        login=os.getenv("RABBITMQ_USERNAME"),
        password=os.getenv("RABBITMQ_PASSWORD"),
    )

    async with connection:
        # 创建通道
        channel = await connection.channel()
        # 声明交换机
        exchange = await channel.declare_exchange(
            rabbitmq_config["KB_DOCUMENT_PARSE_EXCHANGE"],
            type='direct',
            durable=True
        )

        # 主队列
        main_queue = await channel.declare_queue(
            rabbitmq_config["KB_DOCUMENT_PARSE_QUEUE"],
            durable=True
        )
        await main_queue.bind(
            exchange=exchange,
            routing_key=rabbitmq_config["KB_DOCUMENT_PARSE_ROUTING_KEY"]
        )

        # 重试队列
        retry_queue = await channel.declare_queue(
            rabbitmq_config["KB_DOCUMENT_PARSE_RETRY_QUEUE"],
            durable=True,
            arguments={
                "x-message-ttl": int(rabbitmq_config["KB_DOCUMENT_PARSE_RETRY_TTL_MS"]),
                "x-dead-letter-exchange": rabbitmq_config["KB_DOCUMENT_PARSE_EXCHANGE"],
                "x-dead-letter-routing-key": rabbitmq_config["KB_DOCUMENT_PARSE_ROUTING_KEY"]
            }
        )
        await retry_queue.bind(
            exchange=exchange,
            routing_key=rabbitmq_config["KB_DOCUMENT_PARSE_RETRY_ROUTING_KEY"]
        )

        # 死信队列 (DLQ)
        dlq_queue = await channel.declare_queue(
            rabbitmq_config["KB_DOCUMENT_PARSE_DLQ_QUEUE"],
            durable=True
        )
        await dlq_queue.bind(
            exchange=exchange,
            routing_key=rabbitmq_config["KB_DOCUMENT_PARSE_DLQ_ROUTING_KEY"]
        )

        # 限制并发未确认消息，防止 create_task 爆量
        await channel.set_qos(prefetch_count=10)
        # 开始消费
        await main_queue.consume(partial(on_message, exchange=exchange))
        logger.info("开始监听文档解析消息")
        # 保持连接
        await asyncio.Future()  # 永久运行


def vectorization(chunks: List[str]) -> List[List[float]]:
    """
    向量化文本片段
    Args: chunks: 文本片段列表
    Returns: List[List[float]]: 向量化后的文本片段列表
    """
    vectors = []
    for chunk in chunks:
        vector = embed_model.embed_query(chunk)
        vectors.append(vector)
    return vectors

def update_task_status(
        task_id: int, status: TaskStatusEnum, trace_id: str, current_step: TaskStepEnum,
        retry_count: int = None, cost_ms: int = None ,result_json: str = None,
        error_code: str = None, error_message: str = None):
    """
    更新任务状态
    :param task_id: 任务id
    :param status: 任务状态
    :param trace_id: 跟踪id
    :param current_step: 当前步骤
    :param retry_count: 重试次数
    :param cost_ms: 耗时
    :param result_json: 结果json
    :param error_code: 错误码
    :param error_message: 错误信息
    :return:
    """
    from sqlalchemy import text
    from sqlalchemy.orm import Session

    if task_id is None:
        raise ValueError("update_task_status失败: task_id不能为空")
    if not status:
        raise ValueError("update_task_status失败: status不能为空")
    if not current_step:
        raise ValueError("update_task_status失败: current_step不能为空")

    engine = create_engine(
        f"postgresql+psycopg://{os.getenv('PG_USER')}:{os.getenv('PG_PASSWORD')}@{os.getenv('PG_HOST')}:{os.getenv('PG_PORT')}/{os.getenv('PG_DB')}"
    )

    stmt = text(
        """
        UPDATE kb_index_task
        SET
            status = CAST(:status AS varchar),
            current_step = :current_step,
            trace_id = COALESCE(:trace_id, trace_id),
            retry_count = COALESCE(:retry_count, retry_count),
            cost_ms = COALESCE(:cost_ms, cost_ms),
            result_json = COALESCE(:result_json, result_json),
            error_code = COALESCE(:error_code, error_code),
            error_message = COALESCE(:error_message, error_message),
            finished_at = CASE 
                        WHEN CAST(:status AS varchar) IN ('SUCCESS','FAILED','CANCELLED') THEN NOW()
                        ELSE finished_at
            END,
            updated_at = NOW()
        WHERE id = :task_id
        """
    )

    with Session(engine) as session:
        result = session.execute(
            stmt,
            {
                "task_id": int(task_id),
                "status": status.value,
                "current_step": current_step.value,
                "trace_id": trace_id,
                "retry_count": retry_count,
                "cost_ms": cost_ms,
                "result_json": result_json,
                "error_code": error_code,
                "error_message": error_message,
            },
        )
        if result.rowcount == 0:
            session.rollback()
            raise ValueError(f"update_task_status失败: 未找到任务 id={task_id}")
        session.commit()

    logger.info("update_task_status成功: task_id=%s, status=%s, step=%s", task_id, status.value, current_step.value)


def save_vectors(
        vectors: List[List[float]], chunk_ids: List[int], doc_id: int,course_id: int):
    """
    按当前结构将向量写入 PostgreSQL 的 kb_chunk_embedding。
    Args: vectors: 向量化后的文本片段列表
    Args: chunk_ids: 文本片段ID列表
    Args: doc_id: 文档ID
    Args: course_id: 课程ID
    """
    from sqlalchemy import text
    from sqlalchemy.orm import Session

    if not vectors:
        logger.info("需要存储的向量为空")
        return 0
    if not chunk_ids:
        raise ValueError("保存向量失败: chunk_ids 为空")
    if len(vectors) != len(chunk_ids):
        raise ValueError(f"保存向量失败: vectors({len(vectors)})和chunk_ids({len(chunk_ids)})长度不匹配")

    # 数据库连接
    engine = create_engine(
        f"postgresql+psycopg://{os.getenv('PG_USER')}:{os.getenv('PG_PASSWORD')}@{os.getenv('PG_HOST')}:{os.getenv('PG_PORT')}/{os.getenv('PG_DB')}"
    )

    with Session(engine) as session:
        # 先查已有记录（按 chunk_id ），后续做 update / insert 分流
        existed_stmt = text(
            """
            SELECT id, chunk_id
            FROM kb_chunk_embedding
            WHERE chunk_id = ANY(:chunk_ids)
              AND is_deleted = 0
            """
        )
        existed_rows = session.execute(
            existed_stmt,
            {"chunk_ids": chunk_ids},
        ).mappings().all()
        existed_chunk_ids = {int(row["chunk_id"]) for row in existed_rows}

        insert_stmt = text(
            """
            INSERT INTO kb_chunk_embedding
                (chunk_id, doc_id, course_id, embedding_dim, embedding, is_deleted, created_at, updated_at)
            VALUES
                (:chunk_id, :doc_id, :course_id, :embedding_dim, CAST(:embedding AS vector), 0, NOW(), NOW())
            """
        )
        update_stmt = text(
            """
            UPDATE kb_chunk_embedding
            SET embedding_dim = :embedding_dim,
                embedding = CAST(:embedding AS vector),
                updated_at = NOW(),
                is_deleted = 0
            WHERE chunk_id = :chunk_id
            """
        )

        affected = 0
        for idx, vector in enumerate(vectors):
            if not isinstance(vector, list) or len(vector) == 0:
                raise ValueError(f"save_vectors失败: 非法向量索引={idx}")

            chunk_id = int(chunk_ids[idx])
            vector_literal = "[" + ",".join(str(float(x)) for x in vector) + "]"
            params = {
                "chunk_id": chunk_id,
                "doc_id": int(doc_id),
                "course_id": int(course_id),
                "embedding_dim": int(len(vector)),
                "embedding": vector_literal,
            }

            if chunk_id in existed_chunk_ids:
                session.execute(update_stmt, params)
            else:
                session.execute(insert_stmt, params)
            affected += 1

        session.commit()

    logger.info("save_vectors成功, affected=%s, doc_id=%s, course_id=%s", affected, doc_id, course_id)
    return affected


def save_chunks(
        chunks: List[str],doc_id: int,task_id: int,mime_type: MimeTypeEnum,course_id:
        int,source_refs: Optional[List[str]] = None,) -> List[int]:
    """
    保存文本片段
    Args: chunks: 文本片段列表
    Args: task_id: 任务ID
    Args: mime_type: MIME 类型
    Args: course_id: 课程ID
    Args: source_refs: 来源引用列表
    Returns: List[int]: 保存的文本片段ID列表
    """
    from hashlib import sha256
    from sqlalchemy import text
    from sqlalchemy.orm import Session

    if not chunks:
        logger.info("save_chunks跳过: chunks为空")
        return []
    if doc_id is None or course_id is None:
        raise ValueError("save_chunks失败: doc_id/course_id 不能为空")
    if source_refs is not None and len(source_refs) != len(chunks):
        raise ValueError("save_chunks失败: source_refs 长度须与 chunks 一致")

    # 数据库连接
    engine = create_engine(
        f"postgresql+psycopg://{os.getenv('PG_USER')}:{os.getenv('PG_PASSWORD')}@{os.getenv('PG_HOST')}:{os.getenv('PG_PORT')}/{os.getenv('PG_DB')}"
    )

    # 根据 MIME 粗分模态
    mime_value = ""
    if isinstance(mime_type, MimeTypeEnum):
        mime_value = mime_type.value
    else:
        mime_value = str(mime_type or "").lower()
    if mime_value.startswith("image/"):
        modality = "IMAGE"
    elif mime_value.startswith("audio/"):
        modality = "AUDIO"
    elif mime_value.startswith("video/"):
        modality = "VIDEO"
    elif mime_value.startswith("text/"):
        modality = "TEXT"
    else:
        modality = "MIXED"

    insert_stmt = text(
        """
        INSERT INTO kb_chunk
            (chunk_no, doc_id, task_id, course_id, chapter_id, knowledge_point_id, chunk_index,
             content, content_hash, token_count, char_count, start_offset, end_offset, page_no,
             modality, source_type, source_ref, language, vector_status, quality_score, is_deleted, created_at, updated_at)
        VALUES
            (:chunk_no, :doc_id, :task_id, :course_id, NULL, NULL, :chunk_index,
             :content, :content_hash, 0, :char_count, 0, 0, :page_no,
             :modality, 'UPLOAD', :source_ref, 'Chinese', 'SUCCESS', 0.00, 0, NOW(), NOW())
        RETURNING id
        """
    )

    def _page_no_from_ref(ref: Optional[str]) -> int:
        if not ref:
            return 0
        m = re.search(r"#page=(\d+)", ref)
        return int(m.group(1)) if m else 0

    refs_aligned = source_refs if source_refs is not None else [None] * len(chunks)
    chunk_ids: List[int] = []
    with Session(engine) as session:
        for idx, (chunk, chunk_source) in enumerate(zip(chunks, refs_aligned)):
            content = (chunk or "").strip()
            if not content:
                continue

            content_hash = sha256(content.encode("utf-8")).hexdigest()
            chunk_no = f"CHK-{doc_id}-{idx + 1:06d}"
            source_ref = chunk_source or f"doc:{doc_id}#chunk:{idx}"
            params = {
                "chunk_no": chunk_no,
                "doc_id": int(doc_id),
                "task_id": int(task_id) if task_id is not None else None,
                "course_id": int(course_id),
                "chunk_index": idx,
                "content": content,
                "content_hash": content_hash,
                "char_count": len(content),
                "modality": modality,
                "source_ref": source_ref,
                "page_no": _page_no_from_ref(chunk_source),
            }
            new_id = session.execute(insert_stmt, params).scalar_one()
            chunk_ids.append(int(new_id))

        session.commit()

    logger.info("save_chunks成功, doc_id=%s, task_id=%s, chunks=%s", doc_id, task_id, len(chunk_ids))
    return chunk_ids

asyncio.run(main())

if __name__ == "__main__":
    asyncio.run(main())
