package cn.hhh.campusmind.common.utils;

import cn.hhh.campusmind.common.exceptions.NoAuthException;

/**
 *  工具类
 *  在threadLocal中保存用户信息
 */
public class UserContext {

    private static final ThreadLocal<String> TL = new ThreadLocal<>();

    public static void setUser(String userId) {
        TL.set(userId);
    }

    public static Long getUserId() {
        String id = TL.get();
        if (id == null || id.isEmpty()) {
           throw new NoAuthException("用户未登录");
        }
        return Long.parseLong(id);
    }

    public static void removeUser() {
        TL.remove();
    }

}
