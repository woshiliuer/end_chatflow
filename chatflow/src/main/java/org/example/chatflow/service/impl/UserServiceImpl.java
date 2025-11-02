package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.chatflow.common.constants.JwtConstant;
import org.example.chatflow.common.constants.OssConstant;
import org.example.chatflow.common.constants.RedisConstants;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.common.enums.Gender;
import org.example.chatflow.common.exception.BusinessException;
import org.example.chatflow.model.dto.User.GetVerfCodeDTO;
import org.example.chatflow.model.dto.User.LoginDTO;
import org.example.chatflow.model.dto.User.RecoverPasswordDTO;
import org.example.chatflow.model.dto.User.RegisterDTO;
import org.example.chatflow.model.dto.User.UpdateUserInfoDTO;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.model.vo.UserByEmailVO;
import org.example.chatflow.model.vo.UserInfoVO;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.service.UserService;
import org.example.chatflow.support.CurrentUserAccessor;
import org.example.chatflow.factory.VerifyCodeStrategyFactory;
import org.example.chatflow.strategy.VerifyCodeStrategy;
import org.example.chatflow.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author by zzr
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BcryptUtil bcryptUtil;
    private final RedisUtil redisUtil;
    private final AliOssUtil aliOssUtil;
    private final VerifyCodeStrategyFactory verifyCodeStrategyFactory;
    private final CurrentUserAccessor currentUserAccessor;

    /**
     * 登录
     */
    @Override
    public CurlResponse<String> login(LoginDTO dto) {
        String email = StringUtils.trimToEmpty(dto.getEmail());
        String password = dto.getPassword();
        User user = userRepository.findByEmail(email);
        //查询账号是否存在
        Long userId = user.getId();
        VerifyUtil.isTrue(user == null || userId == null || userId <= 0, ErrorCode.USER_NOT_EXISTS);
        //验证密码是否正确
        VerifyUtil.isFalse(bcryptUtil.matches(password,user.getPassword()),ErrorCode.USER_PASSWORD_ERROR);
        //生成token
        try {
            Map<String,Object> claims =  new HashMap<>();
            claims.put(JwtConstant.USER_ID, user.getId());
            claims.put(JwtConstant.NICKNAME, user.getNickname());
            String token = jwtUtil.createToken(claims);
            return CurlResponse.success(token);
        }catch (Exception e){
            log.error("生成token失败, 用户ID: {}", user.getId(), e);
            throw new BusinessException(ErrorCode.USER_TOKEN_GRN_ERROR);
        }
    }


    /**
     * 获取验证码
     */
    @Override
    public CurlResponse<String> getVerfCode(GetVerfCodeDTO dto) {
        VerifyCodeStrategy strategy = verifyCodeStrategyFactory.getStrategy(dto.getVerfCodeType());
        return strategy.process(dto);
    }

    /**
     * 注册
     */
    @Override
    public CurlResponse<String> register(RegisterDTO dto) {
        //验证用户是否重复
        String email = StringUtils.trimToEmpty(dto.getEmail());
        VerifyUtil.isTrue(userRepository.existsByEmail(email),ErrorCode.USER_EXISTS);
        // 验证验证码
        String redisKey = RedisKeyUtil.buildKey(RedisConstants.REGISTER_VERIFY_CODE_KEY_PREFIX, email);
        String verfCode = Optional.ofNullable((String) redisUtil.get(redisKey)).orElse("");
        VerifyUtil.isFalse(verfCode.equals(dto.getVerificationCode()),ErrorCode.VERIFICATION_CODE_ERROR);

        String rawPassword = dto.getPassword();
        //校验密码格式
        checkPassword(rawPassword);
        //加密
        String password = bcryptUtil.hash(rawPassword);
        //创建新用户
        User user = RegisterDTO.RegisterDTOMapper.INSTANCE.toUser(dto);
        user.setAvatarUrl(OssConstant.DEFAULT_AVATAR);
        user.setPassword(password);
        //性别默认男
        user.setGender(Gender.MALE.getCode());
        //个性签名默认空
        user.setSignature("");
        boolean result = userRepository.save(user);
        VerifyUtil.ensureOperationSucceeded(result, ErrorCode.ADD_USER_FAIL);
        //删除验证码
        redisUtil.del(redisKey);
        return CurlResponse.success("注册成功");
    }

    /**
     * 获取用户信息
     */
    @Override
    public CurlResponse<UserInfoVO> getUserInfo() {
        Long userId = ThreadLocalUtil.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTS));

        UserInfoVO userInfoVO = UserInfoVO.UserInfoVOMapper.INSTANCE.toVO(user);
        userInfoVO.setAvatarFullUrl(OssConstant.buildFullUrl(user.getAvatarUrl()));

        return CurlResponse.success(userInfoVO);
    }

    /**
     * 根据邮箱获取用户信息
     */
    @Override
    public CurlResponse<UserByEmailVO> getUserInfoByEmail(String param) {
        User user = userRepository.findByEmail(param);
        VerifyUtil.isTrue(user == null, ErrorCode.USER_NOT_EXISTS);
        UserByEmailVO  userByEmailVO = UserByEmailVO.UserByEmailVOMapper.INSTANCE.toVO(user);
        userByEmailVO.setAvatarFullUrl(OssConstant.buildFullUrl(user.getAvatarUrl()));
        return CurlResponse.success(userByEmailVO);
    }

    /**
     * 上传头像
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CurlResponse<String> uploadAvatar(MultipartFile file) {
        VerifyUtil.isTrue(file.isEmpty(),ErrorCode.FILE_IS_NULL);
        User user = checkUserIsExists();
        String url = "";
        try {

            url = aliOssUtil.upload(file.getBytes(), buildAvatarFileName(
                    file,user.getId()
            ));
            //删除原来的
            String avatar = user.getAvatarUrl();
            if (!OssConstant.DEFAULT_AVATAR.equals(avatar)) {}
                aliOssUtil.delete(avatar);
            //更新url
            user.setAvatarUrl(aliOssUtil.toObjectKey(url));
            userRepository.update(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return CurlResponse.success(url);
    }

    @Override
    public CurlResponse<String> updateUserInfo(UpdateUserInfoDTO dto) {
        User user = checkUserIsExists();

        String nickname = dto.getNickname();
        if (StringUtils.isNotBlank(nickname)) {
            user.setNickname(nickname);
        }

        Integer gender = dto.getGender();
        if (gender != null) {
            Gender genderEnum = Gender.fromCode(gender);
            if (genderEnum != null) {
                user.setGender(genderEnum.getCode());
            }
        }

        String signature = dto.getSignature();
        if (StringUtils.isNotBlank(signature)) {
            user.setSignature(signature);
        }

        VerifyUtil.ensureOperationSucceeded(userRepository.update(user), ErrorCode.UPDATE_USER_INFO_FAIL);
        if (nickname != null) {
            ThreadLocalUtil.setUserNickname(nickname);
        }
        return CurlResponse.success("保存成功");
    }

    @Override
    public CurlResponse<String> recoverPassword(RecoverPasswordDTO dto) {
        User user = checkUserIsExists(dto.getEmail());
        //检查验证码是否正确
        String redisKey = RedisKeyUtil.buildKey(RedisConstants.RECOVER_VERIFY_CODE_KEY_PREFIX, user.getEmail());
        String verfCode = Optional.ofNullable((String) redisUtil.get(redisKey)).orElse("");
        VerifyUtil.isFalse(verfCode.equals(dto.getVerfCode()),ErrorCode.VERIFICATION_CODE_ERROR);
        //验证两次密码是否相等
        String newPassword = dto.getNewPassword();
        String newPasswordConfirm = dto.getNewPasswordConfirm();
        VerifyUtil.isFalse(newPassword.equals(newPasswordConfirm),ErrorCode.CONFIRM_PASSWORD_ERROR);
        //校验密码格式
        checkPassword(newPassword);
        //更新为密码
        String password = bcryptUtil.hash(newPassword);
        user.setPassword(password);
        VerifyUtil.ensureOperationSucceeded(userRepository.update(user), ErrorCode.UPDATE_USER_INFO_FAIL);
        return CurlResponse.success("成功重置密码");
    }

    private User checkUserIsExists(){
        return currentUserAccessor.getCurrentUser();
    }

    private User checkUserIsExists(String email){
        User user = userRepository.findByEmail(email);
        VerifyUtil.isTrue(user == null, ErrorCode.USER_NOT_EXISTS);
        return user;
    }

    private void checkPassword(String rawPassword) {
        VerifyUtil.isTrue(rawPassword.length() < 8 || rawPassword.length() > 12, ErrorCode.PASSWORD_LENGTH_ERROR);
        VerifyUtil.isFalse(rawPassword.matches("^[a-zA-Z0-9]+$"), ErrorCode.PASSWORD_MUST_NUM_ENG);
    }

    private String buildAvatarFileName(MultipartFile file,Long userId) {
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 生成唯一文件名：用户ID + 时间戳 + 随机数 + 后缀
        String fileName = "avatar/" + userId + "_" + System.currentTimeMillis()
                + "_" + (int)(Math.random() * 10000) + suffix;
        return fileName;
    }
}
