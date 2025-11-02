package org.example.chatflow.common.enums;

import lombok.Getter;



/**
 * @author zzr
 */
@Getter
public enum ConversationStatus {

    /**
     * 1 正常（默认显示在会话列表）
     */
    NORMAL(1, "正常"),

    /**
     * 2 隐藏（用户“删除会话”，再次发消息可恢复）
     */
    HIDDEN(2, "隐藏"),

    /**
     * 3 常用（收藏）
     */
    FAVORITE(3, "常用");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态描述
     */
    private final String description;

    ConversationStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据 code 获取枚举对象
     */
    public static ConversationStatus fromCode(int code) {
        for (ConversationStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

}

