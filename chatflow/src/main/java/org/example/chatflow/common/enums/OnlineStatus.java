package org.example.chatflow.common.enums;

import lombok.Getter;

/**
 * @author by zzr
 * @desc 在线状态
 */
@Getter
public enum OnlineStatus {
    ONLINE(1, "在线"),
    OFFLINE(2, "离线");
    private final Integer code;
    private final String desc;

    OnlineStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
