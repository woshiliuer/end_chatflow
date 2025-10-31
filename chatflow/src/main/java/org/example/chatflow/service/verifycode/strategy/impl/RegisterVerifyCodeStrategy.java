package org.example.chatflow.service.verifycode.strategy.impl;

import org.example.chatflow.common.constants.RedisConstants;
import org.example.chatflow.common.enums.VerfCodeType;
import org.example.chatflow.utils.RedisUtil;
import org.example.chatflow.service.verifycode.strategy.AbstractVerifyCodeStrategy;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * 注册流程验证码处理器。
 */
@Component
public class RegisterVerifyCodeStrategy extends AbstractVerifyCodeStrategy {

    public RegisterVerifyCodeStrategy(RedisUtil redisUtil,
                                      JavaMailSender mailSender,
                                      MailProperties mailProperties) {
        super(redisUtil, mailSender, mailProperties);
    }

    @Override
    public VerfCodeType supportType() {
        return VerfCodeType.REGISTER;
    }

    @Override
    protected String redisKeyPrefix() {
        return RedisConstants.REGISTER_VERIFY_CODE_KEY_PREFIX;
    }

    @Override
    protected String mailSubject() {
        return "Chatflow 注册验证码";
    }

    @Override
    protected String mailBody(String code) {
        return "您的验证码为 " + code + "，请在 1 分钟内完成注册。";
    }
}
