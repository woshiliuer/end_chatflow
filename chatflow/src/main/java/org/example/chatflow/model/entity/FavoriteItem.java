package org.example.chatflow.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.chatflow.common.entity.BaseEntity;

/**
 * 用户收藏项
 * @TableName favorite_item
 */
@TableName(value = "favorite_item")
@Data
public class FavoriteItem extends BaseEntity<Long> {

    /**
     * 所属用户ID(收藏夹归属)
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 收藏类型：1文本 2表情
     */
    @TableField("item_type")
    private Integer itemType;

    /**
     * 文本内容
     */
    @TableField("text_content")
    private String textContent;

    /**
     * 来源类型：1单聊 2群聊 3其他
     */
    @TableField("source_type")
    private Integer sourceType;

    /**
     * 发送人ID
     */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 发送时间
     */
    @TableField("send_time")
    private Long sendTime;

    /**
     * 群聊ID（source_type=2时使用）
     */
    @TableField("group_id")
    private Long groupId;

    @TableField(value = "update_user_id", fill = FieldFill.UPDATE)
    private Long updateUserId;

    @TableField(value = "update_by", fill = FieldFill.UPDATE)
    private String updateBy;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private Long updateTime;
}
