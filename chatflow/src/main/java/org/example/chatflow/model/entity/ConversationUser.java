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
     * 角色：1普通成员 2管理员 3群主
     */
    @TableField("role")
    private Integer role;

    /**
     * 加入时间
     */
    @TableField("join_time")
    private Long joinTime;

    /**
     * 最后已读消息序号
     */
    @TableField("last_read_seq")
    private Long lastReadSeq;

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
