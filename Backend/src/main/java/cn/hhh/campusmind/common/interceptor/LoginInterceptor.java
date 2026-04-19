package cn.hhh.campusmind.common.interceptor;

import cn.hhh.campusmind.common.constants.RedisConstants;
import cn.hhh.campusmind.common.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * 拦截器
 * 没有登录的请求将只能访问部分接口
 * 同时获取已经登录用户的信息,并放入到threadLocal中
 */
public class LoginInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;

    public LoginInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null) {
            return false;
        }

        String userId = redisTemplate.opsForValue().get(RedisConstants.USER_TOKEN + token);
        if (userId == null) {
            //解析token失败,返回未登录错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 解析token成功,将用户id放入threadLocal
        UserContext.setUser(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.removeUser();
    }
}
