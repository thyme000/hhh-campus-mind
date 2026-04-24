import argparse
import importlib
import logging
import sys


def _patch_legacy_config_usage() -> None:
    """
    兼容旧代码中把 rabbitmq_config 当函数调用的写法：
    rabbitmq_config("PROFILE_UPDATE_QUEUE")
    """
    from src.config import config as cfg

    if isinstance(cfg.rabbitmq_config, dict):
        raw = cfg.rabbitmq_config
        cfg.rabbitmq_config = lambda key: raw.get(key)


def start_mq_consumer() -> None:
    _patch_legacy_config_usage()
    # 注意：该模块含有启动消费的顶层逻辑，导入即启动
    importlib.import_module("src.mq.profile_update_consumer")


def main() -> int:
    parser = argparse.ArgumentParser(prog="backend-ai")
    parser.add_argument(
        "--mode",
        choices=["mq"],
        default="mq",
        help="启动模式：mq=启动画像更新MQ消费者",
    )
    args = parser.parse_args()

    logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
    logging.info("backend-ai starting, mode=%s", args.mode)

    try:
        if args.mode == "mq":
            start_mq_consumer()
            return 0
        logging.error("不支持的启动模式: %s", args.mode)
        return 2
    except KeyboardInterrupt:
        logging.info("收到中断信号，正在退出")
        return 0
    except Exception as ex:
        logging.exception("启动失败: %s", ex)
        return 1


if __name__ == "__main__":
    sys.exit(main())

