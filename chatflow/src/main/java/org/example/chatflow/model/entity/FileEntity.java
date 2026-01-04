package org.example.chatflow.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.chatflow.common.entity.BaseEntity;

import java.math.BigDecimal;

@TableName(value = "file")
@Data
public class FileEntity extends BaseEntity<Long> {

    @TableField("source_type")
    private String sourceType;

    @TableField("source_id")
    private Long sourceId;

    @TableField("file_type")
    private String fileType;

    @TableField("file_name")
    private String fileName;

    @TableField("file_size")
    private Long fileSize;

    @TableField("file_path")
    private String filePath;

    @TableField("file_desc")
    private String fileDesc;

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
