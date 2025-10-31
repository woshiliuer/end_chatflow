package org.example.chatflow.common.enums;

import lombok.Getter;

/**
 * @author by zzr
 */
@Getter
public enum RequestStatus {

    PENDING(0, "待处理"),
    APPROVED(1, "已同意"),
    REJECTED(2, "已拒绝");

    private final Integer code;
    private final String desc;

    RequestStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
