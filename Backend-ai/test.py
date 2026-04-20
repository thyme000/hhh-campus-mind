import pika

def callback(ch, method, properties, body):
    """消息回调函数：处理接收到的消息"""
    print(f" [x] 接收到消息: {body.decode()}")
    # 处理消息的逻辑（例如：保存到数据库、触发业务逻辑等）

def main():
    # 1. 建立与 RabbitMQ 服务器的连接
    connection = pika.BlockingConnection(
        pika.ConnectionParameters('localhost')  # 默认为本地服务器，可替换为实际地址
    )
    channel = connection.channel()  # 创建通道

    # 2. 声明队列（确保队列存在，若不存在则创建）
    channel.queue_declare(queue='hello', durable=False)  # durable=False 表示队列非持久化

    # 3. 绑定回调函数到队列（告诉 RabbitMQ 收到消息时调用 callback）
    channel.basic_consume(
        queue='hello',  # 队列名称
        on_message_callback=callback,  # 回调函数
        auto_ack=True  # auto_ack=True 表示自动确认消息（处理后自动从队列移除）
    )

    print(' [*] 等待消息... 按 Ctrl+C 退出')
    # 4. 启动消费者（阻塞运行，持续监听消息）
    channel.start_consuming()

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print(' [*] 退出消费者')