package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.User.LoginDTO;
import org.example.chatflow.model.dto.User.RegisterDTO;

/**
 * @author by zzr
 */
public interface UserService {

    CurlResponse<String> login(LoginDTO dto);

    CurlResponse<String> register(RegisterDTO dto);

    CurlResponse<String> getVerfCode(String param);
}
