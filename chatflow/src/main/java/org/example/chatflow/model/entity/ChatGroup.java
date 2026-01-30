package org.example.chatflow.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.example.chatflow.common.entity.BaseEntity;
import org.example.chatflow.serializer.LongJsonSerializer;

/**
 * 群聊表实体
 */
@TableName(value = "chat_group")
@Data
public class ChatGroup extends BaseEntity<Long> {

    /**
     * 群名称
     */
    @TableField("group_name")
    private String groupName;

    /**
     * 群简介
     */
    @TableField("introduction")
    private String introduction;

    /**
     * 群公告
     */
    @TableField("announcement")
    private String announcement;

    /**
     * 群主用户 ID
     */
    @TableField("owner_id")
    private Long ownerId;

    /**
     * 群状态（1正常 2解散）
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否删除：0 未删除 1 已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
