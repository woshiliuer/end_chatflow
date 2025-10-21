package org.example.chatflow.common.constants;


/**
 * Enumerates application-wide error codes.
 */
public enum ErrorCode {
    VALIDATION_ERROR("400", "Validation failed"),
    UNAUTHORIZED("401", "Unauthorized"),
    BUSINESS_ERROR("409", "Business rule violation"),
    INTERNAL_ERROR("500", "Internal server error"),
    USER_NOT_EXISTS("1000","用户不存在")
    ;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
