package org.example.chatflow.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.chatflow.common.entity.BaseEntity;

/**
 * 社交动态-主表
 * @TableName social_feed
 */
@TableName(value = "social_feed")
@Data
public class SocialFeed extends BaseEntity<Long> {

    /**
     * 动态正文
     */
    @TableField("content")
    private String content;

    @TableField(value = "update_user_id", fill = FieldFill.UPDATE)
    private Long updateUserId;

    @TableField(value = "update_by", fill = FieldFill.UPDATE)
    private String updateBy;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private Long updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
