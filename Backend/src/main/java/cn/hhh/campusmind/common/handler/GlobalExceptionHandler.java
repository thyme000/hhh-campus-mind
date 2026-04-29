package cn.hhh.campusmind.common.handler;

import cn.hhh.campusmind.common.exceptions.NoAuthException;
import cn.hhh.campusmind.common.exceptions.OssException;
import cn.hhh.campusmind.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理对象存储异常
     * @param e
     * @return
     */
    @ExceptionHandler(OssException.class)
    public Result<?> handleBizException(OssException e) {
        log.error("对象存储异常: {}", e.getMessage(), e);
        return Result.fail("对象存储异常");
    }

    /**
     * 处理用户登录状态异常
     * @param e
     * @return
     */
    @ExceptionHandler(NoAuthException.class)
    public Result<?> handleNoAuthException(NoAuthException e) {
        log.error("无权限异常: {}", e.getMessage(), e);
        return Result.fail("无权限异常");
    }

    /**
     * 处理会话异常
     * @param e
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("会话异常: {}", e.getMessage(), e);
        return Result.fail("会话异常");
    }

    /**
     * 处理其他异常类型
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
    log.error("异常: {}", e.getMessage(), e);
        return Result.fail("系统繁忙,请稍后再试～");
    }
}
