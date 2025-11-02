package org.example.chatflow.strategy;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.chatflow.common.constants.RedisConstants;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.common.exception.BusinessException;
import org.example.chatflow.model.dto.User.GetVerfCodeDTO;
import org.example.chatflow.utils.RedisKeyUtil;
import org.example.chatflow.utils.RedisUtil;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 提供发送验证码的模板流程，实现公共逻辑。
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractVerifyCodeStrategy implements VerifyCodeStrategy {

    private static final String DEFAULT_MAIL_NICKNAME = "Chatflow 邮箱验证";
    private static final String DEFAULT_ENCODING = "UTF-8";

    private final RedisUtil redisUtil;
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    public CurlResponse<String> process(GetVerfCodeDTO dto) {
        String email = normalizeEmail(dto);
        String redisKey = RedisKeyUtil.buildKey(redisKeyPrefix(), email);

        VerifyUtil.isTrue(Boolean.TRUE.equals(redisUtil.hasKey(redisKey)), ErrorCode.VERIFY_CODE_ALREADY_SENT);
        VerifyUtil.isTrue(StringUtils.isBlank(mailProperties.getUsername()), ErrorCode.MAIL_SENDER_NOT_CONFIGURED);

        String code = generateVerifyCode();
        try {
            sendEmail(email, code);
        } catch (MessagingException | UnsupportedEncodingException | MailException ex) {
            log.error("发送验证码失败, 邮箱: {}", email, ex);
            throw new BusinessException(ErrorCode.VERIFY_CODE_SEND_FAILED);
        }

        redisUtil.set(redisKey, code, redisTtl());
        return CurlResponse.success("验证码发送成功");
    }

    protected String normalizeEmail(GetVerfCodeDTO dto) {
        return StringUtils.trimToEmpty(dto.getEmail());
    }

    protected Duration redisTtl() {
        return RedisConstants.VERIFY_CODE_TTL;
    }

    private String generateVerifyCode() {
        int codeValue = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        return StringUtils.leftPad(Integer.toString(codeValue), 6, '0');
    }

    private void sendEmail(String email, String code) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, DEFAULT_ENCODING);
        helper.setFrom(new InternetAddress(mailProperties.getUsername(), mailSenderDisplayName(), DEFAULT_ENCODING));
        helper.setTo(email);
        helper.setSubject(mailSubject());
        helper.setText(mailBody(code), false);
        mailSender.send(message);
    }

    protected String mailSenderDisplayName() {
        return DEFAULT_MAIL_NICKNAME;
    }

    protected abstract String mailSubject();

    protected abstract String mailBody(String code);

    /**
     * @return 当前处理器使用的 Redis Key 前缀
     */
    protected abstract String redisKeyPrefix();
}
