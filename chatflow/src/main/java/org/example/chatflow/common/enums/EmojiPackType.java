package org.example.chatflow.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author by zzr
 */
@Getter
@AllArgsConstructor
public enum EmojiPackType {
    DEFAULT(1,"默认"),
    CUSTOMIZE(2,"自定义"),
    OFFICIAL(3,"官方");
    private final Integer code;
    private final String description;

    public static EmojiPackType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (EmojiPackType emojiPackType : EmojiPackType.values()) {
            if (emojiPackType.code.equals(code)) {
                return emojiPackType;
            }
        }
        return null;
    }
}
