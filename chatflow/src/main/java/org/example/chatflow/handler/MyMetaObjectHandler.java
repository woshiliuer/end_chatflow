package org.example.chatflow.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.reflection.MetaObject;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.utils.ThreadLocalUtil;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author by zzr
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Long.class, System.currentTimeMillis()/1000);
        this.strictInsertFill(metaObject, "createUserId", Long.class, getCurrentUserId());
        this.strictInsertFill(metaObject, "createBy", String.class, getCurrentUserNickname());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Long.class, System.currentTimeMillis());
        this.strictUpdateFill(metaObject, "updateUserId", Long.class, getCurrentUserId());
        this.strictInsertFill(metaObject, "updateBy", String.class, getCurrentUserNickname());
    }

    private Long getCurrentUserId() {
        return ThreadLocalUtil.getUserId();
    }

    private String getCurrentUserNickname() {
        return ThreadLocalUtil.getUserNickname();
    }
}

