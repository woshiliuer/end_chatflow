package org.example.chatflow.common;

/**
 * @author by zzr
 */
import lombok.Data;
@Data
public final class CurlResponse<T>{

    private static final String DEFAULT_SUCCESS_CODE = "200";

    private final String code;
    private final String message;
    private final T data;

    private CurlResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> CurlResponse<T> success(T data) {
        return new CurlResponse<>(DEFAULT_SUCCESS_CODE, "OK", data);
    }

    public static CurlResponse<Void> success() {
        return new CurlResponse<>(DEFAULT_SUCCESS_CODE, "OK", null);
    }

    public static <T> CurlResponse<T> failure(String code, String message) {
        return new CurlResponse<>(code, message, null);
    }

}
