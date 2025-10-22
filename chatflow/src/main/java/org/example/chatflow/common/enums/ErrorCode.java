package org.example.chatflow.common.enums;


/**
 * Enumerates application-wide error codes.
 */
public enum ErrorCode {
    VALIDATION_ERROR("400", "Validation failed"),
    UNAUTHORIZED("401", "Unauthorized"),
    BUSINESS_ERROR("409", "Business rule violation"),
    INTERNAL_ERROR("500", "服务器内部错误"),
    USER_NOT_EXISTS("1000","用户不存在"),
    USER_PASSWORD_ERROR("1001","用户密码错误"),
    USER_TOKEN_GRN_ERROR("1002","用户token生成失败"),
    USER_EXISTS("1003","用户已存在"),
    MAIL_SENDER_NOT_CONFIGURED("1004","邮件发送账号未配置"),
    VERIFY_CODE_SEND_FAILED("1005","验证码发送失败"),
    VERIFY_CODE_ALREADY_SENT("1006","验证码已发送，请稍后再试"),
    VERIFICATION_CODE_ERROR("1007","验证码错误" ),
    ADD_USER_FAIL("1008","新增用户失败"),
    PASSWORD_LENGTH_ERROR("1009","密码长度必须大于等于8或小于等于12"),
    PASSWORD_MUST_NUM_ENG("1010","密码必须是数字或英文")
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
