package org.example.chatflow.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.chatflow.common.entity.BaseEntity;

import java.time.LocalDateTime;


/**
 * 好友申请表
 * @TableName friend_request
 */
@TableName(value = "friend_request")
@Data
public class FriendRequest extends BaseEntity<Long> {
    /**
     * 申请方用户ID
     */
    @TableField("requester_id")
    private Long requesterId;

    /**
     * 接收方用户ID
     */
    @TableField("receiver_id")
    private Long receiverId;

    /**
     * 申请附言
     */
    @TableField("apply_message")
    private String applyMessage;

    /**
     * 申请状态：0待处理 1已同意 2已拒绝
     */
    @TableField("request_status")
    private Integer requestStatus;

    /**
     * 处理时间
     */
    @TableField("handled_at")
    private LocalDateTime handledAt;
}
