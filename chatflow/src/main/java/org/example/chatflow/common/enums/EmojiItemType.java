package org.example.chatflow.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author by zzr
 */
@Getter
@AllArgsConstructor
public enum EmojiItemType {
    UNICODE(1, "unicode"),
    STATIC_IMAGE(2, "静态图"),
    GIF(3, "动图");

    private final Integer code;
    private final String description;

    public static EmojiItemType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (EmojiItemType emojiItemType : EmojiItemType.values()) {
            if (emojiItemType.code.equals(code)) {
                return emojiItemType;
            }
        }
        return null;
    }
}
