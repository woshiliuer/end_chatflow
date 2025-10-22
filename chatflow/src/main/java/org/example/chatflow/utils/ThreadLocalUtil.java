package org.example.chatflow.utils;

/**
 * 保存当前线程用户信息的上下文工具类。
 */
public final class ThreadLocalUtil {

    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();

    private ThreadLocalUtil() {
    }

    public static void setUserId(Long userId) {
        CURRENT_USER.set(userId);
    }

    public static Long getUserId() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
