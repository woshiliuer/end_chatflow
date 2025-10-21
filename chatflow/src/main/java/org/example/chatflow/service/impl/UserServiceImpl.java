package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.User.LoginDTO;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by zzr
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    /**
     * 登录
     */
    @Override
    public CurlResponse<String> login(LoginDTO dto) {
        //查询账号是否存在
        return null;
    }
}

