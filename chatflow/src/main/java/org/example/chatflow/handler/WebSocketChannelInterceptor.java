package org.example.chatflow.handler;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.chatflow.common.constants.JwtConstant;
import org.example.chatflow.service.OnlineUserService;
import org.example.chatflow.utils.JwtUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * WebSocket消息通道拦截器
 * 用于在STOMP消息层面进行认证，并将用户信息保存到会话属性中
 * @author by zzr
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    private final JwtUtil jwtUtil;
    private final OnlineUserService onlineUserService;
    
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        // 性能优化：只拦截CONNECT命令，其他消息直接放行
        // 原因：
        // 1. 用户信息在CONNECT时已保存到Principal，整个会话期间可复用
        // 2. 本系统消息通过HTTP REST API发送（走AuthInterceptor认证），不通过STOMP SEND命令
        // 3. 避免每条SUBSCRIBE/ACK/DISCONNECT等消息都触发不必要的判断
        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message; // 快速放行非CONNECT消息
        }
        
        // 处理CONNECT命令，进行一次性认证并保存用户信息
        try {
            String token = extractToken(accessor);
            if (StringUtils.isBlank(token)) {
                log.warn("WebSocket CONNECT消息缺少token, sessionId={}", accessor.getSessionId());
                return message;
            }
            
            // 解析JWT获取用户信息
            Claims claims = jwtUtil.parseToken(token);
            Object userIdClaim = claims.get(JwtConstant.USER_ID);
            Object nicknameClaim = claims.get(JwtConstant.NICKNAME);
            
            if (userIdClaim == null) {
                log.warn("WebSocket CONNECT消息token中缺少用户ID, sessionId={}", accessor.getSessionId());
                return message;
            }
            
            Long userId = parseUserId(userIdClaim);
            String nickname = nicknameClaim != null ? nicknameClaim.toString() : "";
            
            // 检查连接数限制（在用户上线前检查）
            if (!onlineUserService.canUserConnect(userId)) {
                int currentCount = onlineUserService.getUserSessionCount(userId);
                log.warn("用户连接数超限，拒绝连接: userId={}, nickname={}, 当前连接数={}, sessionId={}", 
                        userId, nickname, currentCount, accessor.getSessionId());
                // 返回null会拒绝连接
                return null;
            }
            
            // 将用户信息存入STOMP会话属性中（会话期间持久化）
            accessor.getSessionAttributes().put(JwtConstant.USER_ID, userId);
            accessor.getSessionAttributes().put(JwtConstant.NICKNAME, nickname);
            
            // 创建并设置Principal（用户标识）
            // Principal是Spring Security推荐的用户标识方式，会自动关联到整个WebSocket会话
            Principal principal = new Principal() {
                @Override
                public String getName() {
                    return userId.toString();
                }
            };
            accessor.setUser(principal);
            
            log.info("WebSocket CONNECT认证成功: userId={}, nickname={}, sessionId={}", 
                    userId, nickname, accessor.getSessionId());
            
        } catch (Exception e) {
            log.error("WebSocket CONNECT认证失败: sessionId={}, error={}", 
                    accessor.getSessionId(), e.getMessage());
            // 认证失败，拒绝连接
            return null;
        }
        
        return message;
    }
    
    /**
     * 从STOMP头部提取token
     */
    private String extractToken(StompHeaderAccessor accessor) {
        // 从Authorization头部获取
        String authorization = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
        if (StringUtils.isNotBlank(authorization)) {
            return authorization.startsWith(BEARER_PREFIX)
                ? authorization.substring(BEARER_PREFIX.length()).trim()
                : authorization.trim();
        }
        return null;
    }
    
    private Long parseUserId(Object userIdClaim) {
        if (userIdClaim instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(userIdClaim.toString());
    }
}

