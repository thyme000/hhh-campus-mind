package cn.hhh.campusmind.common.constants;

import lombok.Data;

@Data
public class RedisConstants {
    // 用户登录token凭证
    public static final String USER_TOKEN = "user:token:";
    public static final Long USER_TOKEN_EXPIRE = 24L;
}
