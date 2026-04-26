package cn.hhh.campusmind.common.pool;

import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class ExecutorPool {
    public static final ExecutorService EXECUTOR = new ThreadPoolExecutor(
            10, 10, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.AbortPolicy());

    /**
     * 提交任务到线程池
     * @param task 任务
     * @return 任务执行结果
     */
    public <T> Future<T> submit(Callable<T> task) {
        return EXECUTOR.submit(task);
    }

    /**
     * 提交任务到线程池
     * @param task 任务
     * @return 任务执行结果
     */
    public Future<?> submit(Runnable task) {
        return EXECUTOR.submit(task);
    }
}
