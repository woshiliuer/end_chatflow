package org.example.chatflow.service;

import org.example.chatflow.model.dto.OnlineUserDTO;
import org.example.chatflow.model.entity.User;

import java.util.List;

/**
 * 在线用户管理服务接口
 * @author by zzr
 */
public interface OnlineUserService {
    
    /**
     * 用户上线
     * @param sessionId WebSocket会话ID
     * @param user 用户信息
     */
    void userOnline(String sessionId, User user);
    
    /**
     * 用户下线
     * @param userId WebSocket会话ID
     */
    void userOffline(Long userId);
    
    /**
     * 根据会话ID获取用户ID
     * @param sessionId WebSocket会话ID
     * @return 用户ID
     */
    Long getUserIdBySessionId(String sessionId);
    
    /**
     * 根据用户ID获取在线信息
     * @param userId 用户ID
     * @return 在线用户信息
     */
    OnlineUserDTO getOnlineUser(Long userId);
    
    /**
     * 获取所有在线用户
     * @return 在线用户列表
     */
    List<OnlineUserDTO> getAllOnlineUsers();
    
    /**
     * 获取在线用户数量
     * @return 在线用户数
     */
    int getOnlineUserCount();
    
    /**
     * 判断用户是否在线
     * @param userId 用户ID
     * @return 是否在线
     */
    boolean isUserOnline(Long userId);
    
    /**
     * 更新用户活跃时间
     * @param sessionId WebSocket会话ID
     */
    void updateLastActiveTime(String sessionId);
    
    /**
     * 获取用户当前的连接数
     * @param userId 用户ID
     * @return 连接数
     */
    int getUserSessionCount(Long userId);
    
    /**
     * 检查用户是否可以建立新连接（未达到最大连接数限制）
     * @param userId 用户ID
     * @return 是否可以建立新连接
     */
    boolean canUserConnect(Long userId);
}
