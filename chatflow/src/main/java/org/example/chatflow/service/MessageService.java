package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.SendMessageDTO;
import org.example.chatflow.model.vo.MessageVO;

import java.util.List;

/**
 * @author by zzr
 */
public interface MessageService {
    /**
     * 获取会话消息列表
     * @param param 会话ID
     * @return 消息列表
     */
    CurlResponse<List<MessageVO>> messageList(Long param);
    
    /**
     * 发送消息
     * @param dto 发送消息DTO
     * @return 消息信息
     */
    CurlResponse<MessageVO> sendMessage(SendMessageDTO dto);
    
    /**
     * 标记会话消息为已读
     * 将当前用户在指定会话的所有消息标记为已读
     * @param conversationId 会话ID
     * @return 响应结果
     */
    CurlResponse<Void> markAsRead(Long conversationId);
}
