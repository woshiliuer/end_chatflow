package org.example.chatflow.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Redis key helper utilities.
 */
public final class RedisKeyUtil {

    public static String buildKey(String prefix, String suffix) {
        return prefix + suffix;
    }
}
