package org.example.chatflow.utils;

import org.example.chatflow.common.constants.ErrorCode;
import org.example.chatflow.common.exception.BusinessException;

/**
 * @author by zzr
 */
public class VerifyUtil {

    /**
     * 对传入的条件判断如果等于false，报异常
     * @param flag
     * @param errorEnum
     */
    public static void isTrue(boolean flag, ErrorCode errorEnum) {
        if (!flag) {
            throw new BusinessException(errorEnum);
        }
    }

    /**
     * 对传入的条件判断如果等于true，报异常
     * @param flag
     * @param errorEnum
     */
    public static void isFalse(boolean flag, ErrorCode errorEnum) {
        if (flag) {
            throw new BusinessException(errorEnum);
        }
    }

    /**
     * 对传入的条件判断如果等于false，报异常
     * @param flag
     * @param message
     */
    public static void isFalse(boolean flag, String message) {
        if (!flag) {
            throw new BusinessException(message);
        }
    }

    /**
     * 对传入的条件判断如果等于true，报异常
     * @param flag
     * @param message
     */
    public static void isTrue(boolean flag, String message) {
        if (flag) {
            throw new BusinessException(message);
        }
    }
}
