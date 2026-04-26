package cn.hhh.campusmind.common.constants;

import lombok.Data;

@Data
public class RedisConstants {
    // 用户登录token凭证
    public static final String USER_TOKEN = "user:token:";
    public static final Long USER_TOKEN_EXPIRE = 24L;

    // 用户上下文(区分不同会话)
    public static final String USER_SESSION_CONTEXT = "user:context:";
    public static final Long USER_SESSION_CONTEXT_EXPIRE = 1L;
}
