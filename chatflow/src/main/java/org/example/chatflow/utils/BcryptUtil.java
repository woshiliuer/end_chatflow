package org.example.chatflow.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 基于 BCrypt 的密码散列工具。
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BcryptUtil {

    private final PasswordEncoder passwordEncoder;

    /**
     * 使用 BCrypt 计算密码摘要。
     *
     * @param rawPassword 原始密码
     * @return BCrypt 哈希字符串
     */
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 校验原始密码与哈希是否匹配。
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 已存储的 BCrypt 哈希
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
