package org.example.chatflow.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 操作工具类：封装令牌生成与解析的常见逻辑。
 */
@Component
public class JwtUtil {

    /** 配置文件中的原始密钥字符串。 */
    private final String secretValue;
    /** 令牌默认有效期（分钟）。 */
    private final long expirationMinutes;

    private SecretKey secretKey;
    private Duration tokenTtl;
    private JwtParser jwtParser;

    public JwtUtil(@Value("${chatflow.jwt.secret}") String secretValue,
                   @Value("${chatflow.jwt.expiration-minutes:30}") long expirationMinutes) {
        this.secretValue = secretValue;
        this.expirationMinutes = expirationMinutes;
    }

    @PostConstruct
    void init() {
        if (secretValue == null || secretValue.trim().isEmpty()) {
            throw new IllegalStateException("Property 'chatflow.jwt.secret' must be configured");
        }
        byte[] keyBytes = secretValue.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 64) {
            throw new IllegalStateException("JWT secret must be at least 64 bytes for HS512 signing");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.tokenTtl = expirationMinutes > 0 ? Duration.ofMinutes(expirationMinutes) : Duration.ZERO;
        this.jwtParser = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build();
    }

    /**
     * 生成签名的 JWT。
     *
     * @param claims  自定义负载数据（可选）
     * @param subject 令牌主体（可选）
     * @return 已签名的 JWT 字符串
     */
    public String createToken(Map<String, Object> claims, String subject) {
        Instant now = Instant.now();
        JwtBuilder builder = Jwts.builder()
            .setClaims(claims != null ? new HashMap<>(claims) : new HashMap<>())
            .setIssuedAt(Date.from(now))
            .signWith(secretKey, SignatureAlgorithm.HS512);
        if (subject != null && !subject.isBlank()) {
            builder.setSubject(subject);
        }
        if (!tokenTtl.isZero()) {
            builder.setExpiration(Date.from(now.plus(tokenTtl)));
        }
        return builder.compact();
    }

    public String createToken(Map<String, Object> claims) {
        return createToken(claims, null);
    }

    /**
     * 解析 JWT 并返回其中的 Claims。
     *
     * @param token JWT 字符串
     * @return 解析后的 Claims
     */
    public Claims parseToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    /**
     * Returns remaining lifetime in seconds for provided token.
     * @param token JWT token string
     * @return seconds until expiration; 0 if already expired or no expiration present
     */
    public long getRemainingSeconds(String token) {
        Claims claims = parseToken(token);
        Date expiration = claims.getExpiration();
        if (expiration == null) {
            return 0L;
        }
        long seconds = Duration.between(Instant.now(), expiration.toInstant()).getSeconds();
        return Math.max(seconds, 0L);
    }
}
