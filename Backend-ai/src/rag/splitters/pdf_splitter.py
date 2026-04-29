import asyncio
import re
from typing import Optional, List, Any, Dict, Union

from langchain_core.documents import Document
from langchain_core.embeddings import Embeddings
from langchain_text_splitters import RecursiveCharacterTextSplitter

from src.config.config import rag_config


class AsyncPdfSplitter:
    """
    异步PDF分割器类
    核心功能：
    - 将PDF文件分割成多个片段
    - 保留标题、段落、列表层级等结构
    - TODO保证片段语义完整，避免把一个观点拆成多段 (后续实现)
    - TODO使用嵌入模型结合余弦相似度判断语义完整性 (后续实现)
    """

    def __init__(self,
                 chunk_size: Optional[int] = None,
                 chunk_overlap: Optional[int] = None,
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
        default_chunk_size = int(rag_config["pdf_chunk_size"])
        default_chunk_overlap = int(rag_config["pdf_chunk_overlap"])
        default_separators = rag_config["pdf_separators"]
        self.page_split_tokens = rag_config["pdf_page_split_tokens"]
        self.enable_page_mode = bool(rag_config["pdf_enable_page_mode"])

        self.chunk_size = chunk_size if chunk_size is not None else default_chunk_size
        self.chunk_overlap = chunk_overlap if chunk_overlap is not None else default_chunk_overlap
        self.separators = separators or default_separators
        self.embedding_model = embedding_model


        # 初始化递归字符分割器
        self.splitter = (RecursiveCharacterTextSplitter(
            chunk_size=self.chunk_size,
            chunk_overlap=self.chunk_overlap,
            separators=self.separators
        ))

    async def split_text(self, text: str, doc_id: Optional[int] = None) -> List[Document]:
        """
        分割文本成多个片段。
        返回 List[Document]。
        """
        if not text or not text.strip():
            return []
        if not self.enable_page_mode:
            return await asyncio.to_thread(self.splitter.split_text, text)

        effective_id = doc_id if doc_id is not None else 0
        return await asyncio.to_thread(self._split_pdf_text_by_page, text, effective_id)

    async def split_documents(self, documents: List[Document]) -> List[Document]:
        """
        分割文档列表
        Args:documents: 文档对象列表
        Returns: List[Document]: 分割后的文档对象列表
        """
        if not documents:
            return []
        if not self.enable_page_mode:
            return await asyncio.to_thread(self.splitter.split_documents, documents)
        return await asyncio.to_thread(self._split_pdf_documents_by_page, documents)

    def _split_pdf_text_by_page(self, text: str, doc_id: int = 0) -> List[Document]:
        """
        按页面分割PDF文本，并为每个分割后的片段添加页级来源信息。
        """
        pages = self._split_pages(text)
        chunks: List[str] = []
        source_refs: List[str] = []
        for page_no, page_text in enumerate(pages, start=1):
            cleaned = self._clean_page_text(page_text)
            if not cleaned:
                continue
            page_chunks = self.splitter.split_text(cleaned)
            for chunk in page_chunks:
                normalized = chunk.strip()
                if not normalized:
                    continue
                chunks.append(normalized)
                source_refs.append(f"doc_id={doc_id}#page={page_no}")

        documents = []
        for chunk, source_ref in zip(chunks, source_refs):
            doc = Document(
                page_content=chunk,
                metadata={"source_ref": source_ref}
            )
            documents.append(doc)
        return documents

    def _split_pdf_documents_by_page(self, documents: List[Document]) -> List[Document]:
        """
        按页面分割PDF文档对象列表，并为每个分割后的片段添加详细的元数据
        :param documents: PDF文档对象列表，每个对象应包含page_content和metadata属性
        :return: 分割后的文档对象列表，每个对象包含原始元数据和新增的页级信息
        """
        split_docs: List[Document] = []
        for doc in documents:
            # 获取文档内容
            text = getattr(doc, "page_content", "")
            # 获取文档元数据
            metadata = dict(getattr(doc, "metadata", {}) or {})
            pages = self._split_pages(text)
            for page_no, page_text in enumerate(pages, start=1):
                cleaned = self._clean_page_text(page_text)
                # 如果清理后文本为空，跳过当前页
                if not cleaned:
                    continue
                page_chunks = self.splitter.split_text(cleaned)
                for chunk_index, chunk in enumerate(page_chunks, start=1):
                    normalized = chunk.strip()
                    if not normalized:
                        continue
                    chunk_meta = dict(metadata)
                    chunk_meta["page_no"] = page_no
                    chunk_meta["chunk_in_page"] = chunk_index
                    # 保留页级来源信息，便于后续回溯与引用
                    chunk_meta["source_ref"] = f"{chunk_meta.get('source', 'pdf')}#page={page_no}"
                    split_docs.append(Document(page_content=normalized, metadata=chunk_meta))
        return split_docs

    def _split_pages(self, text: str) -> List[str]:
        """
        将PDF文本按页分割成多个页面文本
        :param text: 完整的PDF文本内容
        :return: 按页分割后的文本列表，每个元素对应一个页面
        """
        pages = [text]
        for token in self.page_split_tokens:
            if not token:
                continue
            next_pages: List[str] = []
            for p in pages:
                next_pages.extend(p.split(token))
            pages = next_pages
        # 兜底：兼容一些解析器输出的 "---- Page N ----"
        if len(pages) == 1:
            pages = re.split(r"\n\s*-{2,}\s*Page\s+\d+\s*-{2,}\s*\n", text, flags=re.IGNORECASE)
        return pages

    @staticmethod
    def _clean_page_text(text: str) -> str:
        """简单清洗：规整空白并去掉首尾杂音"""
        cleaned = text.replace("\u00a0", " ")
        cleaned = re.sub(r"\n{3,}", "\n\n", cleaned)
        cleaned = re.sub(r"[ \t]{2,}", " ", cleaned)
        return cleaned.strip()
