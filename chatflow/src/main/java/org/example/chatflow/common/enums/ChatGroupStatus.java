package org.example.chatflow.common.enums;

import lombok.Getter;

/**
 * @author by zzr
 */
@Getter
public enum ChatGroupStatus {
    NORMAL(1, "正常"),
    DISSOLVED(2, "解散");

    private final int code;
    private final String description;

    ChatGroupStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
