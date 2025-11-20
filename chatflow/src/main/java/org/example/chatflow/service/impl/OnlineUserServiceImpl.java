package org.example.chatflow.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.model.dto.OnlineUserDTO;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.service.OnlineUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

/**
 * 在线用户管理服务实现
 * 支持多端登录，每个用户可以建立多个WebSocket连接（不同设备）
 * @author by zzr
 */
@Slf4j
@Service
public class OnlineUserServiceImpl implements OnlineUserService {
    
    /**
     * 单个用户最大连接数（支持多端登录：手机、电脑、平板等）
     */
    private static final int MAX_CONNECTIONS_PER_USER = 5;
    
    /**
     * 存储在线用户信息: sessionId -> OnlineUserDTO
     */
    private final Map<String, OnlineUserDTO> sessionUserMap = new ConcurrentHashMap<>();
    
    /**
     * 存储用户ID到会话ID集合的映射: userId -> Set<sessionId>
     * 支持多端登录：一个用户可以有多个会话（手机、电脑等）
     */
    private final Map<Long, Set<String>> userSessionsMap = new ConcurrentHashMap<>();
    
    @Override
    public void userOnline(String sessionId, User user) {
        if (sessionId == null || user == null || user.getId() == null) {
            log.warn("用户上线参数无效: sessionId={}, user={}", sessionId, user);
            return;
        }
        
        Long userId = user.getId();
        
        // 检查该用户当前的连接数
        Set<String> userSessions = userSessionsMap.computeIfAbsent(userId, 
            k -> ConcurrentHashMap.newKeySet()); // 使用线程安全的Set
        
        if (userSessions.size() >= MAX_CONNECTIONS_PER_USER) {
            log.warn("用户[{}]连接数已达上限: {}/{}, 拒绝新连接 sessionId={}", 
                userId, userSessions.size(), MAX_CONNECTIONS_PER_USER, sessionId);
            throw new IllegalStateException(
                String.format("连接数超过限制，当前已有%d个连接，最多允许%d个", 
                    userSessions.size(), MAX_CONNECTIONS_PER_USER));
        }
        
        LocalDateTime now = LocalDateTime.now();
        OnlineUserDTO onlineUser = new OnlineUserDTO(
            userId,
            user.getNickname(),
            user.getEmail(),
            user.getAvatarUrl(),
            sessionId,
            now,
            now
        );
        
        // 保存会话信息
        sessionUserMap.put(sessionId, onlineUser);
        userSessions.add(sessionId);
        
        log.info("用户上线成功: userId={}, nickname={}, sessionId={}, 当前连接数={}/{}", 
            userId, user.getNickname(), sessionId, userSessions.size(), MAX_CONNECTIONS_PER_USER);
        log.info("当前在线用户总数: {}, 活跃会话总数: {}", 
            userSessionsMap.size(), sessionUserMap.size());
    }
    
    @Override
    public void userOffline(String sessionId) {
        if (sessionId == null) {
            return;
        }
        
        OnlineUserDTO onlineUser = sessionUserMap.remove(sessionId);
        if (onlineUser != null) {
            Long userId = onlineUser.getUserId();
            
            // 从用户会话集合中移除这个会话
            Set<String> userSessions = userSessionsMap.get(userId);
            if (userSessions != null) {
                userSessions.remove(sessionId);
                
                // 如果该用户没有任何会话了，移除整个映射
                if (userSessions.isEmpty()) {
                    userSessionsMap.remove(userId);
                    log.info("用户完全下线: userId={}, nickname={}", userId, onlineUser.getNickname());
                } else {
                    log.info("用户部分下线: userId={}, nickname={}, sessionId={}, 剩余连接数={}", 
                        userId, onlineUser.getNickname(), sessionId, userSessions.size());
                }
            }
            
            log.info("当前在线用户总数: {}, 活跃会话总数: {}", 
                userSessionsMap.size(), sessionUserMap.size());
        }
    }
    
    @Override
    public Long getUserIdBySessionId(String sessionId) {
        OnlineUserDTO onlineUser = sessionUserMap.get(sessionId);
        return onlineUser != null ? onlineUser.getUserId() : null;
    }
    
    @Override
    public OnlineUserDTO getOnlineUser(Long userId) {
        Set<String> userSessions = userSessionsMap.get(userId);
        if (userSessions == null || userSessions.isEmpty()) {
            return null;
        }
        
        // 返回该用户的第一个会话信息（多端登录时任选一个）
        String sessionId = userSessions.iterator().next();
        return sessionUserMap.get(sessionId);
    }
    
    @Override
    public List<OnlineUserDTO> getAllOnlineUsers() {
        return new ArrayList<>(sessionUserMap.values());
    }
    
    @Override
    public int getOnlineUserCount() {
        // 返回在线用户数（去重），而非会话数
        return userSessionsMap.size();
    }
    
    @Override
    public boolean isUserOnline(Long userId) {
        return userSessionsMap.containsKey(userId);
    }
    
    @Override
    public void updateLastActiveTime(String sessionId) {
        OnlineUserDTO onlineUser = sessionUserMap.get(sessionId);
        if (onlineUser != null) {
            onlineUser.setLastActiveTime(LocalDateTime.now());
        }
    }
    
    @Override
    public int getUserSessionCount(Long userId) {
        Set<String> userSessions = userSessionsMap.get(userId);
        return userSessions != null ? userSessions.size() : 0;
    }
    
    @Override
    public boolean canUserConnect(Long userId) {
        int currentCount = getUserSessionCount(userId);
        return currentCount < MAX_CONNECTIONS_PER_USER;
    }
}


