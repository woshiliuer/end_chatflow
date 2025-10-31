package org.example.chatflow.service.support;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.common.exception.BusinessException;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.utils.ThreadLocalUtil;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 提供当前登录用户的便捷访问。
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CurrentUserAccessor {

    private final UserRepository userRepository;

    /**
     * 获取当前登录用户，若不存在则抛出业务异常。
     *
     * @return 当前用户
     */
    public User getCurrentUser() {
        Long userId = ThreadLocalUtil.getUserId();
        VerifyUtil.isTrue(userId == null, ErrorCode.USER_NOT_EXISTS);
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTS));
    }
}

