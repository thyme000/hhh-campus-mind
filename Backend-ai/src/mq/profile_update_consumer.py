import json
import logging
import os

import pika
from dotenv import load_dotenv

from src.common.http_client import java_backend_http_client
from src.common.logger_handler import logger
from src.config.config import rabbitmq_config
from src.graph.profile_update_workflow import run_profile_update_workflow
from src.mq.messages.profile_update_message import TriggerUpdateEventMessage

load_dotenv()

credentials = pika.PlainCredentials(os.getenv("RABBITMQ_USERNAME"), os.getenv("RABBITMQ_PASSWORD"))
connection = pika.BlockingConnection(pika.ConnectionParameters(
    host =os.getenv("RABBITMQ_HOST"),port=int(os.getenv("RABBITMQ_PORT")),credentials=credentials
))
channel = connection.channel()

channel.exchange_declare(exchange=rabbitmq_config["PROFILE_UPDATE_EXCHANGE"], exchange_type='direct', durable=True)
channel.queue_declare(queue=rabbitmq_config["PROFILE_UPDATE_QUEUE"], durable=True)  # 如果是持久化队列就是True

# 绑定
channel.queue_bind(
    queue=rabbitmq_config["PROFILE_UPDATE_QUEUE"],
    exchange=rabbitmq_config["PROFILE_UPDATE_EXCHANGE"],
    routing_key=rabbitmq_config["PROFILE_UPDATE_ROUTING_KEY"]
)

def callback(ch, method, properties, body):
    """
    更新学生画像
    :param ch:
    :param method:
    :param properties:
    :param body:
    :return:
    """
    try:
        body_parse = body.decode('utf-8')
        logger.info("内容是" + body_parse)
        # 启动学生画像更新工作流
        result = run_profile_update_workflow(TriggerUpdateEventMessage.from_dict(json.loads(body_parse)))
        # 调用java接口更新学生画像
        java_backend_http_client.callback_profile_result(result.to_dict())
        # 确认消费成功
        ch.basic_ack(delivery_tag=method.delivery_tag)
    except Exception as e:
        logger.error(f"消费更新学生画像消息失败, {e}")


channel.basic_consume(rabbitmq_config["PROFILE_UPDATE_QUEUE"], callback)
logging.info("开始监听更新学生画像消息")
channel.start_consuming()
