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
import org.example.chatflow.common.constants.FileSourceTypeConstant;
import org.example.chatflow.model.dto.common.FileCommonDTO;
import org.example.chatflow.model.dto.User.GetVerfCodeDTO;
import org.example.chatflow.model.dto.User.LoginDTO;
import org.example.chatflow.model.dto.User.RecoverPasswordDTO;
import org.example.chatflow.model.dto.User.RegisterDTO;
import org.example.chatflow.model.dto.User.UpdateUserInfoDTO;
import org.example.chatflow.model.entity.EmojiPack;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.model.entity.UserEmojiPack;
import org.example.chatflow.model.vo.UserByEmailVO;
import org.example.chatflow.model.vo.UserInfoVO;
import org.example.chatflow.repository.EmojiPackRepository;
import org.example.chatflow.repository.UserEmojiPackRepository;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.service.FileService;
import org.example.chatflow.service.OnlineUserService;
import org.example.chatflow.service.UserService;
import org.example.chatflow.factory.VerifyCodeStrategyFactory;
import org.example.chatflow.strategy.VerifyCodeStrategy;
import org.example.chatflow.support.CurrentUserAccessor;
import org.example.chatflow.utils.AliOssUtil;
import org.example.chatflow.utils.BcryptUtil;
import org.example.chatflow.utils.JwtUtil;
import org.example.chatflow.utils.RedisKeyUtil;
import org.example.chatflow.utils.RedisUtil;
import org.example.chatflow.utils.ThreadLocalUtil;
import org.example.chatflow.utils.VerifyUtil;
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
    private final FileService fileService;
    private final VerifyCodeStrategyFactory verifyCodeStrategyFactory;
    private final CurrentUserAccessor currentUserAccessor;
    private final OnlineUserService onlineUserService;
    private final EmojiPackRepository emojiPackRepository;
    private final UserEmojiPackRepository userEmojiPackRepository;
    /**
     * 登录
     */
    @Override
    public CurlResponse<String> login(LoginDTO dto) {
        String email = StringUtils.trimToEmpty(dto.getEmail());
        String password = dto.getPassword();
        User user = userRepository.findByEmail(email);
        //查询账号是否存在
        VerifyUtil.isTrue(user == null, ErrorCode.USER_NOT_EXISTS);
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
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<String> register(RegisterDTO dto) {
        //验证用户是否重复
        String email = StringUtils.trimToEmpty(dto.getEmail());
        checkEmail(email);
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
        user.setPassword(password);
        //性别默认男
        user.setGender(Gender.MALE.getCode());
        //个性签名默认空
        user.setSignature("");
        boolean result = userRepository.save(user);
        VerifyUtil.ensureOperationSucceeded(result, ErrorCode.ADD_USER_FAIL);

        fileService.updateFile(FileCommonDTO.FileCommonDTOMapper.INSTANCE.toDTO(
                FileSourceTypeConstant.USER_AVATAR,
                user.getId(),
                null,
                null,
                null,
                OssConstant.DEFAULT_AVATAR,
                null
        ));
        //删除验证码
        redisUtil.del(redisKey);
        //默认表情
        EmojiPack defaultEmojiPack = emojiPackRepository.findDefalt();
        UserEmojiPack userEmojiPack = new UserEmojiPack();
        userEmojiPack.setUserId(user.getId());
        userEmojiPack.setPackId(defaultEmojiPack.getId());
        userEmojiPack.setSort(1);
        userEmojiPackRepository.save(userEmojiPack);
        //自定义表情
        EmojiPack cusEmojiPack = new EmojiPack();
        cusEmojiPack.setName("自定义表情");
        cusEmojiPack.setType(2);
        emojiPackRepository.save(cusEmojiPack);
        UserEmojiPack  userEmojiPack1 = new UserEmojiPack();
        userEmojiPack1.setUserId(user.getId());
        userEmojiPack1.setPackId(cusEmojiPack.getId());
        userEmojiPack1.setSort(2);
        userEmojiPackRepository.save(userEmojiPack1);
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
        userInfoVO.setAvatarFullUrl(fileService.getLatestFullUrl(
                FileSourceTypeConstant.USER_AVATAR,
                user.getId(),
                OssConstant.DEFAULT_AVATAR
        ));

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
        userByEmailVO.setAvatarFullUrl(fileService.getLatestFullUrl(
                FileSourceTypeConstant.USER_AVATAR,
                user.getId(),
                OssConstant.DEFAULT_AVATAR
        ));
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
        try {
            String oldPath = fileService.getLatestFilePath(FileSourceTypeConstant.USER_AVATAR, user.getId());
            if (oldPath != null && !oldPath.isBlank() && !OssConstant.DEFAULT_AVATAR.equals(oldPath)) {
                aliOssUtil.delete(oldPath);
            }

            String url = aliOssUtil.upload(file.getBytes(), buildAvatarFileName(
                    file,user.getId()
            ));
            String newPath = AliOssUtil.toObjectKey(url);

            fileService.updateFile(FileCommonDTO.FileCommonDTOMapper.INSTANCE.toDTO(
                    FileSourceTypeConstant.USER_AVATAR,
                    user.getId(),
                    file.getContentType(),
                    file.getOriginalFilename(),
                    file.getSize(),
                    newPath,
                    null
            ));

            return CurlResponse.success(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<Boolean> updateNotificationEnabled(Integer notificationEnabled) {
        VerifyUtil.isTrue(notificationEnabled == null, ErrorCode.VALIDATION_ERROR);
        VerifyUtil.isTrue(notificationEnabled != 1 && notificationEnabled != 2, ErrorCode.VALIDATION_ERROR);

        User user = checkUserIsExists();
        user.setNotificationEnabled(notificationEnabled);
        VerifyUtil.ensureOperationSucceeded(userRepository.update(user), ErrorCode.UPDATE_USER_INFO_FAIL);
        return CurlResponse.success(Boolean.TRUE);
    }

    /**
     * 退出登录：清理在线状态
     */
    @Override
    public CurlResponse<String> logout() {
        Long userId = ThreadLocalUtil.getUserId();
        if (userId != null) {
            onlineUserService.userOffline(userId);
        }
        return CurlResponse.success("退出登录成功");
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

    private void checkEmail(String email) {
        VerifyUtil.isTrue(StringUtils.isBlank(email), "邮箱不能为空");
        // 基础格式校验：local@domain.tld（避免过度严格，先保证明显非法的挡掉）
        String pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        VerifyUtil.isFalse(email.matches(pattern), "邮箱格式不正确");
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
