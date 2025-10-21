package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.constants.ErrorCode;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.User.LoginDTO;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.service.UserService;
import org.example.chatflow.utils.JwtUtils;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.http.HttpRequest;

/**
 * @author by zzr
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    /**
     * 登录
     */
    @Override
    public CurlResponse<String> login(LoginDTO dto) {
        //查询账号是否存在
        String email = dto.getEmail();
        String password = dto.getPassword();
        User user = userRepository.findByEmail(email);
        VerifyUtil.isTrue(user == null || user.getId() == null, ErrorCode.USER_NOT_EXISTS);
        //TODO 验证密码是否正确

        return null;
    }
}

