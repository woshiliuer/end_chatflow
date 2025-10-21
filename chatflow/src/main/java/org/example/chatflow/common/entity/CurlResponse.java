package org.example.chatflow.common.entity;

/**
 * @author by zzr
 */

import lombok.Data;
import org.example.chatflow.common.constants.ErrorCode;

@Data
public final class CurlResponse<T> {

    private static final String DEFAULT_SUCCESS_CODE = "200";
    private static final String DEFAULT_SUCCESS_MESSAGE = "OK";

    private final String code;
    private final String message;
    private final T data;

    private CurlResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> CurlResponse<T> success(T data) {
        return new CurlResponse<>(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, data);
    }

    public static CurlResponse<Void> success() {
        return new CurlResponse<>(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, null);
    }

    public static <T> CurlResponse<T> failure(String code, String message) {
        return new CurlResponse<>(code, message, null);
    }

    public static <T> CurlResponse<T> failure(ErrorCode errorCode) {
        return new CurlResponse<>(errorCode.getCode(), errorCode.getDefaultMessage(), null);
    }

    public static <T> CurlResponse<T> failure(ErrorCode errorCode, String message) {
        return new CurlResponse<>(errorCode.getCode(), message, null);
    }
}
