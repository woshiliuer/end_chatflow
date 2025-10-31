package org.example.chatflow.common.enums;


/**
 * Enumerates application-wide error codes.
 */
public enum ErrorCode {
    VALIDATION_ERROR("400", "参数不合法"),
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
    PASSWORD_MUST_NUM_ENG("1010","密码必须是数字或英文"),
    USER_NOT_LOGIN("1011","用户未登录"),
    FRIEND_REQUEST_ADD_FAIL("1012","好友申请失败"),
    FRIEND_REQUEST_NOT_EXISTS("1013","好友申请不存在"),
    AGREE_FRIEND_FAIL("1014","同意好友申请失败"),
    DISAGREE_FRIEND_FAIL("1015","拒绝好友申请失败"),
    FRIEND_REQUEST_EXISTS("1016","不可重复申请添加好友"),
    REQUESTID_EQUALS_RECEIVERID("1017","不能申请添加自己为好友"),
    SEX_ERROR("1018","性别错误"),
    FILE_IS_NULL("1019","文件为空"),
    UPDATE_USER_INFO_FAIL("1020","保存个人资料失败"),
    CONFIRM_PASSWORD_ERROR("1021","两次密码不相同"),
    FRIEND_RELATION_NOT_EXISTS("1022","好友关系不存在"),
    DELETE_FRIEND_FAIL("1023", "删除好友失败"),
    GROUP_SAVE_FAIL("1024","群聊保存失败" ),
    CONVERSATION_SAVE_FAIL("1025","会话保存失败" ),
    CONVERSATION_USER_SAVE_FAIL("1026","会话成员关系保存失败" ),
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
