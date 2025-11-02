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
    // 创建线程局部变量来控制是否填充
    private static final ThreadLocal<Boolean> FILL_ENABLED = ThreadLocal.withInitial(() -> true);


    @Override
    public void insertFill(MetaObject metaObject) {
        if (!isFillEnabled()) {
            return;
        }
        this.strictInsertFill(metaObject, "createTime", Long.class, System.currentTimeMillis()/1000);
        this.strictInsertFill(metaObject, "createUserId", Long.class, getCurrentUserId());
        this.strictInsertFill(metaObject, "createBy", String.class, getCurrentUserNickname());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (!isFillEnabled()) {
            return;
        }
        this.strictUpdateFill(metaObject, "updateTime", Long.class, System.currentTimeMillis());
        this.strictUpdateFill(metaObject, "updateUserId", Long.class, getCurrentUserId());
        this.strictInsertFill(metaObject, "updateBy", String.class, getCurrentUserNickname());
    }

    private boolean isFillEnabled() {
        return FILL_ENABLED.get();
    }

    private Long getCurrentUserId() {
        return ThreadLocalUtil.getUserId();
    }

    private String getCurrentUserNickname() {
        return ThreadLocalUtil.getUserNickname();
    }


    // 提供静态方法控制填充
    public static void disableFill() {
        FILL_ENABLED.set(false);
    }

    public static void enableFill() {
        FILL_ENABLED.set(true);
    }

    public static void clearFillFlag() {
        FILL_ENABLED.remove();
    }
}

