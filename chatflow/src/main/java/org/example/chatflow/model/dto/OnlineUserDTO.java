package org.example.chatflow.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 在线用户信息DTO
 * @author by zzr
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineUserDTO {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户邮箱
     */
    private String email;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
    
    /**
     * WebSocket会话ID
     */
    private String sessionId;
    
    /**
     * 连接时间
     */
    private LocalDateTime connectTime;
    
    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;
}


