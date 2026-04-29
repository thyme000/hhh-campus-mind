import asyncio
from typing import List, Optional
from langchain_core.documents import Document
from langchain.embeddings.base import Embeddings
from langchain_text_splitters import RecursiveCharacterTextSplitter

from src.config.config import rag_config


class AsyncTextSplitter:
    """
    异步文本分割器类
    核心功能：
    - 将文本分割成多个片段
    - 保留标题、段落、列表层级等结构
    - TODO保证片段语义完整，避免把一个观点拆成多段 (后续实现)
    - TODO使用嵌入模型结合余弦相似度判断语义完整性 (后续实现)
    """

    def __init__(self,
                 chunk_size: int = 1000,
                 chunk_overlap: int = 200,
                 separators: Optional[List[str]] = None,
                 embedding_model: Optional[Embeddings] = None):
        """
        初始化文本分割器
        Args:
            chunk_size: 每个文本片段的最大长度
            chunk_overlap: 片段之间的重叠长度
            separators: 分割符列表，用于分割文本
            embedding_model: 嵌入模型，用于计算语义相似度
        """
        # 默认分割符，按优先级排序
        default_separators = rag_config['txt_separators']
        default_chunk_size = rag_config["txt_chunk_size"]
        default_chunk_overlap = rag_config["txt_chunk_overlap"]
        self.chunk_size = chunk_size or default_chunk_size
        self.chunk_overlap = chunk_overlap or default_chunk_overlap
        self.separators = separators or default_separators
        self.embedding_model = embedding_model

        # 初始化递归字符分割器
        self.splitter = RecursiveCharacterTextSplitter(
            chunk_size=chunk_size,
            chunk_overlap=chunk_overlap,
            separators=self.separators
        )

    async def split_text(self, text: str) -> List[str]:
        """
        分割文本成多个片段
        Args:text: 要分割的文本
        Returns: List[str]: 分割后的文本片段列表
        """
        # 使用递归字符分割器分割文本（同步操作，用to_thread包装）
        chunks = await asyncio.to_thread(self.splitter.split_text, text)

        # TODO提供嵌入模型，进一步优化分割结果

        return chunks

    async def split_documents(self, documents: List[Document]) -> List[Document]:
        """
        分割文档列表
        Args:documents: 文档对象列表
        Returns: List[Document]: 分割后的文档对象列表
        """
        # 使用递归字符分割器分割文档（同步操作，用to_thread包装）
        split_docs = await asyncio.to_thread(self.splitter.split_documents, documents)
        return split_docs



