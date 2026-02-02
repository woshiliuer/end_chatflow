package org.example.chatflow.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.chatflow.common.entity.BaseEntity;

/**
 * 社交动态-点赞表
 * @TableName social_feed_like
 */
@TableName(value = "social_feed_like")
@Data
public class SocialFeedLike extends BaseEntity<Long> {

    @TableField("feed_id")
    private Long feedId;

    @TableField("user_id")
    private Long userId;

    @TableField("status")
    private Integer status;

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
