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
    @TableField("user_id")
    private Long userId;

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
}
