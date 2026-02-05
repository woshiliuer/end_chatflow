package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.User.GetVerfCodeDTO;
import org.example.chatflow.model.dto.User.LoginDTO;
import org.example.chatflow.model.dto.User.RecoverPasswordDTO;
import org.example.chatflow.model.dto.User.RegisterDTO;
import org.example.chatflow.model.dto.User.UpdateUserInfoDTO;
import org.example.chatflow.model.vo.UserByEmailVO;
import org.example.chatflow.model.vo.UserInfoVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author by zzr
 */
public interface UserService {

    CurlResponse<String> login(LoginDTO dto);

    CurlResponse<String> register(RegisterDTO dto);

    CurlResponse<String> getVerfCode(GetVerfCodeDTO dto);

    CurlResponse<UserInfoVO> getUserInfo();

    CurlResponse<UserByEmailVO> getUserInfoByEmail(String param);

    CurlResponse<String> uploadAvatar(MultipartFile file);

    CurlResponse<String> updateUserInfo(UpdateUserInfoDTO dto);

    CurlResponse<String> recoverPassword(RecoverPasswordDTO dto);

    CurlResponse<Boolean> updateNotificationEnabled(Integer notificationEnabled);

    /**
     * 退出登录，清理在线状态
     */
    CurlResponse<String> logout();
}
