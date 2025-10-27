package org.example.chatflow.utils;

import org.example.chatflow.common.enums.ErrorCode;

/**
 * 保存当前线程用户信息的上下文工具类。
 */
public final class ThreadLocalUtil {

    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();

    private static final ThreadLocal<String> CURRENT_USER_NICKNAME = new ThreadLocal<>();

    private ThreadLocalUtil() {
    }

    public static void setUserId(Long userId) {
        CURRENT_USER_ID.set(userId);
    }

    public static void setUserNickname(String nickname) {
        CURRENT_USER_NICKNAME.set(nickname);
    }

    public static Long getUserId() {
        Long userId = CURRENT_USER_ID.get();
        VerifyUtil.isTrue(userId == null, ErrorCode.USER_NOT_LOGIN);
        return userId;
    }

    public static String getUserNickname() {
        String nickname = CURRENT_USER_NICKNAME.get();
        VerifyUtil.isTrue(nickname == null, ErrorCode.USER_NOT_LOGIN);
        return nickname;
    }

    public static void clear() {
        CURRENT_USER_ID.remove();
        CURRENT_USER_NICKNAME.remove();
    }
}
