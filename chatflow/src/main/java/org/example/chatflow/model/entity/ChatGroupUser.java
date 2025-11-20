package org.example.chatflow.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.chatflow.common.entity.BaseEntity;

/**
 * 群聊成员关系实体
 */
@Data
@TableName("chat_group_user")
public class ChatGroupUser extends BaseEntity<Long> {

    /**
     * 群聊ID
     */
    @TableField("group_id")
    private Long groupId;

    /**
     * 成员用户ID
     */
    @TableField("member_id")
    private Long memberId;

    /**
     * 角色：1成员 2管理员 3群主
     */
    @TableField("role")
    private Integer role;

    /**
     * 加入时间（秒时间戳）
     */
    @TableField("join_time")
    private Long joinTime;

    /**
     * 状态：1正常 2已退出/移除
     */
    @TableField("status")
    private Integer status;
}
