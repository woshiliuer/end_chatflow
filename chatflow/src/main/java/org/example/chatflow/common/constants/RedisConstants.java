package org.example.chatflow.common.constants;

import java.time.Duration;

/**
 * Redis key definitions.
 */
public final class RedisConstants {

    private RedisConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String VERIFY_CODE_KEY_PREFIX = "verify-code:";
    public static final Duration VERIFY_CODE_TTL = Duration.ofMinutes(1);

}
