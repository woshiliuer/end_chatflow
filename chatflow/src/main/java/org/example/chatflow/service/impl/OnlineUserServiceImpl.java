package org.example.chatflow.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
 * 在线用户管理服务实现（Redis 版本）
 * 支持多端登录，每个用户可以建立多个 WebSocket 连接
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OnlineUserServiceImpl implements OnlineUserService {

    private static final int MAX_CONNECTIONS_PER_USER = 5;

    private static final String KEY_ALL_SESSIONS = "online:sessions";
    private static final String KEY_USER_SESSIONS_PREFIX = "online:user:sessions:";
    private static final String KEY_SESSION_PREFIX = "online:session:";
    private static final String KEY_USERS = "online:users";

    private static final String FIELD_USER_ID = "userId";
    private static final String FIELD_NICKNAME = "nickname";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_AVATAR = "avatar";
    private static final String FIELD_LAST_ACTIVE = "lastActive";
    private static final long SESSION_TTL_MINUTES = 60L;

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void userOnline(String sessionId, User user) {
        if (sessionId == null || user == null || user.getId() == null) {
            log.warn("用户上线参数无效: sessionId={}, user={}", sessionId, user);
            return;
        }
        Long userId = user.getId();

        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        String userSessionsKey = buildUserSessionsKey(userId);
        Long sessionCount = setOps.size(userSessionsKey);
        long currentCount = sessionCount == null ? 0 : sessionCount;
        if (currentCount >= MAX_CONNECTIONS_PER_USER) {
            throw new IllegalStateException(String.format("连接数超过限制，当前已有%d个连接，最多允许%d个",
                currentCount, MAX_CONNECTIONS_PER_USER));
        }

        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        String sessionKey = buildSessionKey(sessionId);
        Map<String, String> sessionData = new HashMap<>();
        sessionData.put(FIELD_USER_ID, userId.toString());
        sessionData.put(FIELD_NICKNAME, user.getNickname());
        sessionData.put(FIELD_EMAIL, user.getEmail());
        sessionData.put(FIELD_AVATAR, user.getAvatarUrl());
        sessionData.put(FIELD_LAST_ACTIVE, String.valueOf(Instant.now().toEpochMilli()));
        hashOps.putAll(sessionKey, sessionData);
        redisTemplate.expire(sessionKey, SESSION_TTL_MINUTES, TimeUnit.MINUTES);

        setOps.add(KEY_ALL_SESSIONS, sessionId);
        setOps.add(userSessionsKey, sessionId);
        redisTemplate.expire(userSessionsKey, SESSION_TTL_MINUTES, TimeUnit.MINUTES);
        setOps.add(KEY_USERS, String.valueOf(userId));

        log.info("用户上线成功: userId={}, nickname={}, sessionId={}, 当前连接数 {}/{}",
            userId, user.getNickname(), sessionId, currentCount + 1, MAX_CONNECTIONS_PER_USER);
    }

    @Override
    public void userOffline(String sessionId) {
        if (sessionId == null) {
            return;
        }
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        String sessionKey = buildSessionKey(sessionId);
        String userIdStr = (String) hashOps.get(sessionKey, FIELD_USER_ID);

        redisTemplate.delete(sessionKey);
        setOps.remove(KEY_ALL_SESSIONS, sessionId);

        if (userIdStr != null) {
            Long userId = Long.valueOf(userIdStr);
            String userSessionsKey = buildUserSessionsKey(userId);
            setOps.remove(userSessionsKey, sessionId);
            Long remaining = setOps.size(userSessionsKey);
            if (remaining == null || remaining == 0) {
                redisTemplate.delete(userSessionsKey);
                setOps.remove(KEY_USERS, userIdStr);
            }
        }
    }

    @Override
    public Long getUserIdBySessionId(String sessionId) {
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        String userIdStr = (String) hashOps.get(buildSessionKey(sessionId), FIELD_USER_ID);
        return userIdStr == null ? null : Long.valueOf(userIdStr);
    }

    @Override
    public OnlineUserDTO getOnlineUser(Long userId) {
        Set<String> sessions = getUserSessions(userId);
        if (sessions.isEmpty()) {
            return null;
        }
        String sessionId = sessions.iterator().next();
        return getOnlineUserBySession(sessionId);
    }

    @Override
    public List<OnlineUserDTO> getAllOnlineUsers() {
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        Set<String> sessionIds = setOps.members(KEY_ALL_SESSIONS);
        List<OnlineUserDTO> list = new ArrayList<>();
        if (sessionIds != null) {
            for (String sessionId : sessionIds) {
                OnlineUserDTO dto = getOnlineUserBySession(sessionId);
                if (dto != null) {
                    list.add(dto);
                }
            }
        }
        return list;
    }

    @Override
    public int getOnlineUserCount() {
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        Long size = setOps.size(KEY_USERS);
        return size == null ? 0 : size.intValue();
    }

    @Override
    public boolean isUserOnline(Long userId) {
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        Boolean exists = setOps.isMember(KEY_USERS, String.valueOf(userId));
        return exists != null && exists;
    }

    @Override
    public void updateLastActiveTime(String sessionId) {
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        String sessionKey = buildSessionKey(sessionId);
        hashOps.put(sessionKey, FIELD_LAST_ACTIVE, String.valueOf(Instant.now().toEpochMilli()));
        redisTemplate.expire(sessionKey, SESSION_TTL_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public int getUserSessionCount(Long userId) {
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        Long size = setOps.size(buildUserSessionsKey(userId));
        return size == null ? 0 : size.intValue();
    }

    @Override
    public boolean canUserConnect(Long userId) {
        return getUserSessionCount(userId) < MAX_CONNECTIONS_PER_USER;
    }

    private Set<String> getUserSessions(Long userId) {
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        Set<String> sessions = setOps.members(buildUserSessionsKey(userId));
        return sessions == null ? java.util.Collections.emptySet() : sessions;
    }

    private OnlineUserDTO getOnlineUserBySession(String sessionId) {
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        Map<Object, Object> map = hashOps.entries(buildSessionKey(sessionId));
        if (map == null || map.isEmpty()) {
            return null;
        }
        String userIdStr = (String) map.get(FIELD_USER_ID);
        if (userIdStr == null) {
            return null;
        }
        LocalDateTime lastActive = null;
        String lastActiveStr = (String) map.get(FIELD_LAST_ACTIVE);
        if (lastActiveStr != null) {
            try {
                lastActive = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(Long.parseLong(lastActiveStr)), ZoneId.systemDefault());
            } catch (NumberFormatException ignored) {
            }
        }
        return new OnlineUserDTO(
            Long.valueOf(userIdStr),
            (String) map.get(FIELD_NICKNAME),
            (String) map.get(FIELD_EMAIL),
            (String) map.get(FIELD_AVATAR),
            sessionId,
            lastActive,
            lastActive
        );
    }

    private String buildUserSessionsKey(Long userId) {
        return KEY_USER_SESSIONS_PREFIX + userId;
    }

    private String buildSessionKey(String sessionId) {
        return KEY_SESSION_PREFIX + sessionId;
    }
}
