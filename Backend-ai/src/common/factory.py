from abc import ABC, abstractmethod
from typing import Optional
import os
from dotenv import load_dotenv
from langchain_openai import ChatOpenAI

from langchain_core.embeddings import Embeddings
from langchain_core.language_models import BaseChatModel

from src.config.config import model_config
from src.models.xfyun_embedding import XfyunEmbeddings

# 加载环境变量
load_dotenv()

class BaseModelFactory(ABC):
    """基础模型工厂"""

    @abstractmethod
    def generator(self) -> Optional[Embeddings | BaseChatModel]:
        """生成模型"""
        pass


class ChatModelFactory(BaseModelFactory):
    """聊天模型工厂"""
    def generator(self) -> Optional[Embeddings | BaseChatModel]:
        """生成模型"""
        return ChatOpenAI(
            api_key=os.getenv("API_KEY"),
            model=model_config["chat_model_name"],
            base_url="https://spark-api-open.xf-yun.com/v1/",
            default_headers={
                "X-AppId": os.getenv("APP_ID"),
                "X-Api-Secret": os.getenv("API_SECRET")
            },
            temperature=0.7,
            max_tokens=2048
        )


class EmbedModelFactory(BaseModelFactory):
    """嵌入模型工厂"""
    def generator(self) -> Optional[Embeddings | BaseChatModel]:
        """生成模型"""
        return XfyunEmbeddings(
            app_id=os.getenv("APP_ID"),
            embedding_key=os.getenv("EMBEDDING_KEY"),
            api_secret=os.getenv("API_SECRET"),
        )


chat_model = ChatModelFactory().generator()
embed_model = EmbedModelFactory().generator()

reranker_model = None

if __name__ == '__main__':
    result = chat_model.stream("你好你是谁?")
    for chunk in result:
        print(chunk.content,end="")
    # embed = embed_model.embed_query("这段话的内容变成向量化是什么样的")
    # print(embed)
