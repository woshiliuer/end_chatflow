package org.example.chatflow.common.enums;

import lombok.Getter;

@Getter
public enum SocialFeedLikeStatus {

    VALID(1, "有效"),
    CANCELED(2, "取消");

    private final Integer code;
    private final String message;

    SocialFeedLikeStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
