package org.example.chatflow.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.chatflow.common.entity.BaseEntity;

/**
 * 会话用户关系实体
 */
@TableName(value = "conversation_user")
@Data
public class ConversationUser extends BaseEntity<Long> {
    /**
     * 会话ID
     */
    @TableField("conversation_id")
    private Long conversationId;

    /**
     * 参与用户ID
     */
    @TableField("member_id")
    private Long memberId;

    /**
     * 最后已读消息序号
     */
    @TableField("last_read_seq")
    private Long lastReadSeq;

    /**
     * 消息可见起点序号（seq <= visibleSeq 对该成员不可见）
     */
    @TableField("visible_seq")
    private Long visibleSeq;

    /**
     * 最后已读时间（毫秒时间戳）
     */
    @TableField("last_read_time")
    private Long lastReadTime;

    /**
     * 会话状态：1 常规 2 隐藏 3 常用
     */
    @TableField("status")
    private Integer status;
}
