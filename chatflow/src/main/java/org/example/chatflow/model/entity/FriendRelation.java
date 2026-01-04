package org.example.chatflow.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.chatflow.common.entity.BaseEntity;

/**
 * 好友关系表
 * @TableName friend_relation
 */
@TableName(value ="friend_relation")
@Data
public class FriendRelation extends BaseEntity<Long> {
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 好友ID
     */
    @TableField("friend_id")
    private Long friendId;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 删除标记
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
