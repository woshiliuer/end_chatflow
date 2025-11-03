package org.example.chatflow.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author by zzr
 */
@Getter
@AllArgsConstructor
public enum Direction {

    USER_TO_FRIEND(1, "我发出的"),
    FRIEND_TO_USER(2, "收到的");

    private final Integer code;
    private final String description;
    public static Direction fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (Direction direction : Direction.values()) {
            if (direction.code.equals(code)) {
                return direction;
            }
        }
        return null;
    }

}

