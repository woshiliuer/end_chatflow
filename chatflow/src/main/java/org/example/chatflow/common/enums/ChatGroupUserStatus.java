package org.example.chatflow.common.enums;

import lombok.Getter;

/**
 * @author by zzr
 */
@Getter
public enum ChatGroupUserStatus {
    NORMAL(1, "正常"),
    QUIT(2, "已退出"),
    REMOVED(3, "已移除");


    private final int code;
    private final String description;

    ChatGroupUserStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
