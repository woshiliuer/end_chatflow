package org.example.chatflow.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.chatflow.common.entity.BaseEntity;

/**
 * 会话表实体
 */
@TableName(value = "conversation")
@Data
public class Conversation extends BaseEntity<Long> {
    /**
     * 会话类型：0单聊 1群聊
     */
    @TableField("conversation_type")
    private Integer conversationType;

    /**
     * 群聊Id
     */

    @TableField("group_id")
    private Long groupId;

    /**
     * 最新消息ID
     */
    @TableField("last_message_id")
    private Long lastMessageId;

    /**
     * 最新消息时间（时间戳）
     */
    @TableField("last_message_time")
    private Long lastMessageTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
