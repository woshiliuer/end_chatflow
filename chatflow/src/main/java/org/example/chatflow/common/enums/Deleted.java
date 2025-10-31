package org.example.chatflow.common.enums;

import lombok.Getter;

/**
 * @author by zzr
 */
@Getter
public enum Deleted {

    HAS_DELETED(1, "已删除"),
    HAS_NOT_DELETED(2, "未删除");

    private final Integer code;
    private final String message;

    Deleted(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
