package org.example.chatflow.common.enums;

import lombok.Getter;

/**
 * @author by zzr
 */
@Getter
public enum VerfCodeType {

    REGISTER(1, "注册"),
    FORGOT_PASSWORD(2, "找回密码");

    private final int code;
    private final String description;

    VerfCodeType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据 code 获取枚举
     */
    public static VerfCodeType fromCode(int code) {
        for (VerfCodeType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的验证码类型: " + code);
    }
}
