package org.example.chatflow.utils;

import org.example.chatflow.common.enums.ErrorCode;

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
        Long userId = CURRENT_USER.get();
        VerifyUtil.isTrue(userId == null, ErrorCode.USER_NOT_LOGIN);
        return userId;
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
