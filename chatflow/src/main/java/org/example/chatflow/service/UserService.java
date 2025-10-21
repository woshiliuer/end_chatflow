package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.User.LoginDTO;

/**
 * @author by zzr
 */
public interface UserService {

    CurlResponse<String> login(LoginDTO dto);
}
