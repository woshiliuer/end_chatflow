package org.example.chatflow.common.constants;

import java.time.Duration;

/**
 * Redis key definitions.
 */
public final class RedisConstants {

    private RedisConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String REGISTER_VERIFY_CODE_KEY_PREFIX = "register-verify-code:";
    public static final String RECOVER_VERIFY_CODE_KEY_PREFIX = "recover-verify-code:";
    public static final Duration VERIFY_CODE_TTL = Duration.ofMinutes(1);

}
