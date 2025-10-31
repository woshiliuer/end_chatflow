package org.example.chatflow.common.enums;

import lombok.Getter;

/**
 * @author by zzr
 */
@Getter
public enum ConversationType {

    PRIVATE(1, "单聊"),
    GROUP(2, "群聊");

    private final Integer code;
    private final String message;

    ConversationType(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
