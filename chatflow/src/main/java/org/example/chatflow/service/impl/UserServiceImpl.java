package org.example.chatflow.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.chatflow.common.constants.JwtConstant;
import org.example.chatflow.common.constants.OssConstant;
import org.example.chatflow.common.constants.RedisConstants;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.common.exception.BusinessException;
import org.example.chatflow.model.dto.User.LoginDTO;
import org.example.chatflow.model.dto.User.RegisterDTO;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.service.UserService;
import org.example.chatflow.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author by zzr
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BcryptUtil bcryptUtil;
    private final RedisUtil redisUtil;
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    /**
     * 登录
     */
    @Override
    public CurlResponse<String> login(LoginDTO dto) {
        String email = StringUtils.trimToEmpty(dto.getEmail());
        String password = dto.getPassword();
        User user = userRepository.findByEmail(email);
        //查询账号是否存在
        Long userId = user.getId();
        VerifyUtil.isTrue(user == null || userId == null || userId <= 0, ErrorCode.USER_NOT_EXISTS);
        //验证密码是否正确
        VerifyUtil.isFalse(bcryptUtil.matches(password,user.getPassword()),ErrorCode.USER_PASSWORD_ERROR);
        //生成token
        try {
            Map<String,Object> claims =  new HashMap<>();
            claims.put(JwtConstant.USER_ID, user.getId());
            String token = jwtUtil.createToken(claims);
            return CurlResponse.success(token);
        }catch (Exception e){
            log.error("生成token失败, 用户ID: {}", user.getId(), e);
            throw new BusinessException(ErrorCode.USER_TOKEN_GRN_ERROR);
        }
    }


    /**
     * 获取验证码
     */
    @Override
    public CurlResponse<String> getVerfCode(String param) {
        String email = StringUtils.trimToEmpty(param);
        String redisKey = RedisKeyUtil.buildKey(RedisConstants.VERIFY_CODE_KEY_PREFIX, email);
        VerifyUtil.isTrue(Boolean.TRUE.equals(redisUtil.hasKey(redisKey)), ErrorCode.VERIFY_CODE_ALREADY_SENT);
        VerifyUtil.isTrue(StringUtils.isBlank(mailProperties.getUsername()), ErrorCode.MAIL_SENDER_NOT_CONFIGURED);

        int codeValue = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        String code = StringUtils.leftPad(Integer.toString(codeValue), 6, '0');

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(new InternetAddress(mailProperties.getUsername(), "Chatflow 邮箱验证", "UTF-8"));
            helper.setTo(email);
            helper.setSubject("Chatflow 验证码");
            helper.setText("您的验证码为 " + code + "，请在 1 分钟内完成验证。", false);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException | MailException e) {
            log.error("发送验证码失败, 邮箱: {}", email, e);
            throw new BusinessException(ErrorCode.VERIFY_CODE_SEND_FAILED);
        }

        redisUtil.set(redisKey, code, RedisConstants.VERIFY_CODE_TTL);
        return CurlResponse.success("验证码发送成功");
    }

    /**
     * 注册
     */
    @Override
    public CurlResponse<String> register(RegisterDTO dto) {
        //验证用户是否重复
        String email = StringUtils.trimToEmpty(dto.getEmail());
        VerifyUtil.isTrue(userRepository.existsByEmail(email),ErrorCode.USER_EXISTS);
        // 验证验证码
        String redisKey = RedisKeyUtil.buildKey(RedisConstants.VERIFY_CODE_KEY_PREFIX, email);
        String verfCode = Optional.ofNullable((String) redisUtil.get(redisKey)).orElse("");
        VerifyUtil.isFalse(verfCode.equals(dto.getVerificationCode()),ErrorCode.VERIFICATION_CODE_ERROR);

        String rawPassword = dto.getPassword();
        //校验密码格式
        checkPassword(rawPassword);
        //加密
        String password = bcryptUtil.hash(rawPassword);
        //创建新用户
        User user = RegisterDTO.RegisterDTOMapper.INSTANCE.toUser(dto);
        user.setAvatarUrl(OssConstant.DEFAULT_AVATAR);
        user.setPassword(password);
        user.setCreateUserId(0L);
        user.setCreateBy("自行注册");
        user.setCreateTime(System.currentTimeMillis()/1000);
        boolean result = userRepository.save(user);
        VerifyUtil.ensureOperationSucceeded(result, ErrorCode.ADD_USER_FAIL);
        //删除验证码
        redisUtil.del(redisKey);
        return CurlResponse.success("注册成功");
    }

    private void checkPassword(String rawPassword) {
        VerifyUtil.isTrue(rawPassword.length() < 8 || rawPassword.length() > 12, ErrorCode.PASSWORD_LENGTH_ERROR);
        VerifyUtil.isFalse(rawPassword.matches("^[a-zA-Z0-9]+$"), ErrorCode.PASSWORD_MUST_NUM_ENG);
    }
}
