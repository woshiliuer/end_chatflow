package org.example.chatflow.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.serializer.LongJsonSerializer;

/**
 * @author by zzr
 */
@Data
public abstract class BaseEntity<T> extends AbstractEntity {
    public static final String FIELD_ID = "id";
    public static final String CREATE_USER_ID_COLUMN = "create_user_id";
    public static final String CREATE_BY_COLUMN = "create_by";
    public static final String CREATE_TIME_COLUMN = "create_time";
    public static final String CREATE_USER_ID = "createUserId";
    public static final String CREATED_BY = "createBy";
    public static final String CREATE_TIME = "createTime";
    private static final long serialVersionUID = 6169873634279173682L;
    @Schema(
            description = "ID"
    )
    @TableId(
            value = "id",
            type = IdType.ASSIGN_ID
    )
    protected T id;
    @TableField(
            value = "create_user_id",
            fill = FieldFill.INSERT
    )
    @Schema(
            description = "创建人ID"
    )
    protected T createUserId;
    @TableField(
            value = "create_by",
            fill = FieldFill.INSERT
    )
    @Schema(
            description = "创建人名称"
    )
    protected String createBy;
    @JsonSerialize(
            using = LongJsonSerializer.class
    )
    @TableField(
            value = "create_time",
            fill = FieldFill.INSERT
    )
    @Schema(
            description = "创建时间"
    )
    protected Long createTime;

}

