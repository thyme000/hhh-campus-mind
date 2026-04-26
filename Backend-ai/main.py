import argparse
import importlib
import logging
import multiprocessing
import os
import signal
import sys
import time

import uvicorn


def start_mq_consumer() -> None:
    # 注意：该模块含有启动消费的顶层逻辑，导入即启动（阻塞）
    importlib.import_module("src.mq.profile_update_consumer")


def start_generate_session_title_consumer() -> None:
    # 注意：该模块含有启动消费的顶层逻辑，导入即启动（阻塞）
    importlib.import_module("src.mq.generate_session_title_consumer")


def start_fastapi(host: str, port: int) -> None:
    # 显式导入聊天路由，保证路由注册生效
    importlib.import_module("src.router.chat")
    uvicorn.run("src.router.router:app", host=host, port=port, log_level="info")


def _run_target(name: str, target, *args) -> multiprocessing.Process:
    process = multiprocessing.Process(name=name, target=target, args=args)
    process.start()
    logging.info("started process: %s (pid=%s)", name, process.pid)
    return process


def _terminate_all(processes: list[multiprocessing.Process]) -> None:
    for process in processes:
        if process.is_alive():
            process.terminate()
    for process in processes:
        process.join(timeout=5)


def _wait_processes(processes: list[multiprocessing.Process]) -> int:
    try:
        while True:
            dead = [p for p in processes if not p.is_alive()]
            if dead:
                failed = dead[0]
                logging.error("process exited unexpectedly: %s (exitcode=%s)", failed.name, failed.exitcode)
                _terminate_all(processes)
                return 1
            time.sleep(1)
    except KeyboardInterrupt:
        logging.info("received shutdown signal, stopping all processes")
        _terminate_all(processes)
        return 0


def main() -> int:
    parser = argparse.ArgumentParser(prog="backend-ai")
    parser.add_argument(
        "--mode",
        choices=["api", "mq", "all"],
        default="all",
        help="启动模式：api=仅FastAPI, mq=仅MQ消费者, all=FastAPI+全部MQ消费者",
    )
    parser.add_argument("--host", default=os.getenv("API_HOST", "0.0.0.0"), help="FastAPI host")
    parser.add_argument("--port", type=int, default=int(os.getenv("API_PORT", "5000")), help="FastAPI port")
    args = parser.parse_args()

    logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
    logging.info("backend-ai starting, mode=%s", args.mode)

    try:
        processes: list[multiprocessing.Process] = []

        if args.mode in ("api", "all"):
            processes.append(_run_target("fastapi", start_fastapi, args.host, args.port))

        if args.mode in ("mq", "all"):
            processes.append(_run_target("profile_update_consumer", start_mq_consumer))
            processes.append(
                _run_target("generate_session_title_consumer", start_generate_session_title_consumer)
            )

        if not processes:
            logging.error("没有可启动的目标进程")
            return 2

        # 让 Ctrl+C 在主进程处理并广播终止
        signal.signal(signal.SIGINT, signal.default_int_handler)
        return _wait_processes(processes)
    except KeyboardInterrupt:
        logging.info("收到中断信号，正在退出")
        return 0
    except Exception as ex:
        logging.exception("启动失败: %s", ex)
        return 1


if __name__ == "__main__":
    sys.exit(main())

