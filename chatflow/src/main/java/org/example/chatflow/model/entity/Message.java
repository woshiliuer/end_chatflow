package org.example.chatflow.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.chatflow.common.entity.BaseEntity;

/**
 * 消息表实体
 */
@TableName(value = "message")
@Data
public class Message extends BaseEntity<Long> {
    /**
     * 所属会话ID
     */
    @TableField("conversation_id")
    private Long conversationId;

    /**
     * 发送方用户ID
     */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 消息类型：1文本 2图片 3语音
     */
    @TableField("message_type")
    private Integer messageType;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 序号
     */
    @TableField("sequence")
    private Long sequence;

    /**
     * 状态：1正常 2撤回
     */
    @TableField("status")
    private Integer status;

    /**
     * 发送时间
     */
    @TableField("send_time")
    private Long sendTime;
}
