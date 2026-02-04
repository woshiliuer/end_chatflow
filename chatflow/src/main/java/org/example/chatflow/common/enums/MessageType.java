package org.example.chatflow.common.enums;

import lombok.Getter;

/**
 * 消息类型
 */
@Getter
public enum MessageType {
    TEXT(1, "文本"),
    EMOJI(2, "表情");
    private final Integer code;
    private final String desc;

    MessageType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public static MessageType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (MessageType messageType : MessageType.values()) {
            if (messageType.code.equals(code)) {
                return messageType;
            }
        }
        return null;
    }
}
