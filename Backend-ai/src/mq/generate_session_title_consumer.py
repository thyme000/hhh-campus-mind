import json
import logging
import os

import pika
from dotenv import load_dotenv

from src.common.http_client import java_backend_http_client
from src.common.logger_handler import logger
from src.config.config import rabbitmq_config
from src.graph.generate_session_title_workflow import run_generate_session_title_workflow
from src.mq.messages.generate_session_title_message import GenerateSessionTitleMessage

load_dotenv()

credentials = pika.PlainCredentials(os.getenv("RABBITMQ_USERNAME"), os.getenv("RABBITMQ_PASSWORD"))
connection = pika.BlockingConnection(pika.ConnectionParameters(
    host =os.getenv("RABBITMQ_HOST"),port=int(os.getenv("RABBITMQ_PORT")),credentials=credentials
))
channel = connection.channel()

channel.exchange_declare(exchange=rabbitmq_config["GENERATE_SESSION_TITLE_EXCHANGE"], exchange_type='direct', durable=True)
channel.queue_declare(queue=rabbitmq_config["GENERATE_SESSION_TITLE_QUEUE"], durable=True)  # 如果是持久化队列就是True

# 绑定
channel.queue_bind(
    queue=rabbitmq_config["GENERATE_SESSION_TITLE_QUEUE"],
    exchange=rabbitmq_config["GENERATE_SESSION_TITLE_EXCHANGE"],
    routing_key=rabbitmq_config["GENERATE_SESSION_TITLE_ROUTING_KEY"]
)

def callback(ch, method, properties, body):
    try:
        body_parse = body.decode('utf-8')
        logger.info("内容是" + body_parse)
        # 开启生成会话标题工作流
        result = run_generate_session_title_workflow(GenerateSessionTitleMessage.from_dict(json.loads(body_parse)))
        # 回传给java保存会话标题
        java_backend_http_client.callback_generate_session_title_result(result.to_dict())
        # 确认消费成功
        ch.basic_ack(delivery_tag=method.delivery_tag)
    except Exception as e:
        logger.error(f"消费生成会话标题消息失败, {e}")


channel.basic_consume(rabbitmq_config["GENERATE_SESSION_TITLE_QUEUE"], callback)
logging.info("开始监听生成会话标题消息")
channel.start_consuming()