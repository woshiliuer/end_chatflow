package org.example.chatflow.common.constants;

/**
 * Enumerates application-wide error codes.
 */
public enum ErrorCode {
    VALIDATION_ERROR("400", "Validation failed"),
    UNAUTHORIZED("401", "Unauthorized"),
    BUSINESS_ERROR("409", "Business rule violation"),
    INTERNAL_ERROR("500", "Internal server error");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
