package org.example.chatflow.handler;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.chatflow.common.constants.JwtConstant;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.common.exception.BusinessException;
import org.example.chatflow.utils.JwtUtil;
import org.example.chatflow.utils.ThreadLocalUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 在请求进入控制器之前完成身份校验的拦截器。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = resolveToken(request);
        if (StringUtils.isBlank(token)) {
            log.debug("Skip request {}: missing Authorization header", request.getRequestURI());
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        try {
            Claims claims = jwtUtil.parseToken(token);
            Object userIdClaim = claims.get(JwtConstant.USER_ID);
            if (userIdClaim == null) {
                log.debug("Token payload missing userId");
                throw new BusinessException(ErrorCode.UNAUTHORIZED);
            }
            long userId = parseUserId(userIdClaim);
            ThreadLocalUtil.setUserId(userId); // 保存当前登录用户，便于业务层使用
            request.setAttribute(JwtConstant.USER_ID, userId); // 让控制器也能访问
            return true;
        } catch (ExpiredJwtException ex) {
            log.debug("Token expired: {}", ex.getMessage());
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Token invalid", ex);
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //清Threadlocal
        ThreadLocalUtil.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.isNotBlank(authorization)) {
            return authorization.startsWith(BEARER_PREFIX)
                ? authorization.substring(BEARER_PREFIX.length()).trim()
                : authorization.trim();
        }
        return null;
    }

    private long parseUserId(Object userIdClaim) {
        if (userIdClaim instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(userIdClaim.toString());
    }
}
