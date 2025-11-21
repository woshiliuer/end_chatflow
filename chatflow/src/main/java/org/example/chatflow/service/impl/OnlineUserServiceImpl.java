package org.example.chatflow.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.model.dto.OnlineUserDTO;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.service.OnlineUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

/**
 * 在线用户管理服务实现（简化版：仅按用户维度存储在线集合）
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OnlineUserServiceImpl implements OnlineUserService {

    private static final String KEY_USERS = "online:users";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void userOnline(String sessionId, User user) {
        if (sessionId == null || user == null || user.getId() == null) {
            log.warn("用户上线参数无效: sessionId={}, user={}", sessionId, user);
            return;
        }
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        setOps.add(KEY_USERS, String.valueOf(user.getId()));
        log.info("用户上线成功: userId={}, nickname={}, sessionId={}", user.getId(), user.getNickname(), sessionId);
    }

    @Override
    public void userOffline(Long userId) {
        if (userId == null) {
            return;
        }
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        setOps.remove(KEY_USERS, String.valueOf(userId));
        log.info("用户下线: userId={}", userId);
    }

    @Override
    public Long getUserIdBySessionId(String sessionId) {
        // 仅按用户维度存储，不再维护 session -> user 映射
        return null;
    }

    @Override
    public OnlineUserDTO getOnlineUser(Long userId) {
        return isUserOnline(userId)
            ? new OnlineUserDTO(userId, null, null, null, null, null, null)
            : null;
    }

    @Override
    public List<OnlineUserDTO> getAllOnlineUsers() {
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        Set<String> userIds = setOps.members(KEY_USERS);
        List<OnlineUserDTO> list = new ArrayList<>();
        if (userIds != null) {
            for (String userIdStr : userIds) {
                list.add(new OnlineUserDTO(Long.valueOf(userIdStr), null, null, null, null, null, null));
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
        if (userId == null) {
            return false;
        }
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        Boolean exists = setOps.isMember(KEY_USERS, String.valueOf(userId));
        return exists != null && exists;
    }

    @Override
    public void updateLastActiveTime(String sessionId) {
        // 简化版本不做会话维度存储，忽略心跳更新
    }

    @Override
    public int getUserSessionCount(Long userId) {
        return isUserOnline(userId) ? 1 : 0;
    }

    @Override
    public boolean canUserConnect(Long userId) {
        // 不再限制连接数，始终允许
        return true;
    }
}