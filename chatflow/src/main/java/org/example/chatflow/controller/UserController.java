package org.example.chatflow.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.entity.Param;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.common.exception.BusinessException;
import org.example.chatflow.model.dto.User.LoginDTO;
import org.example.chatflow.model.dto.User.RegisterDTO;
import org.example.chatflow.model.vo.UserByEmailVO;
import org.example.chatflow.model.vo.UserInfoVO;
import org.example.chatflow.service.UserService;
import org.example.chatflow.utils.JwtUtil;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * User-related endpoints.
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "密码登录")
    @PostMapping("/login")
    public CurlResponse<String> login(@RequestBody @Validated LoginDTO dto) {
        return userService.login(dto);
    }

    @Operation(summary = "获取验证码", description = "参数传邮箱")
    @PostMapping("/getVerfCode")
    public CurlResponse<String> getVerfCode(@RequestBody @Validated Param<String> param) {
        return userService.getVerfCode(param.getParam());
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public CurlResponse<String> register(@RequestBody RegisterDTO dto) {
        return userService.register(dto);
    }

    @Operation(summary = "验证 token 是否有效")
    @GetMapping("/token/validate")
    public CurlResponse<Boolean> validateToken(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        String token = StringUtils.trimToEmpty(authorization);
        VerifyUtil.isTrue(StringUtils.isBlank(token), ErrorCode.UNAUTHORIZED);
        if (StringUtils.startsWithIgnoreCase(token, BEARER_PREFIX)) {
            token = token.substring(BEARER_PREFIX.length()).trim();
        }
        VerifyUtil.isTrue(StringUtils.isBlank(token), ErrorCode.UNAUTHORIZED);
        try {
            jwtUtil.parseToken(token);
            return CurlResponse.success(Boolean.TRUE);
        } catch (ExpiredJwtException ex) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/getUserInfo")
    public CurlResponse<UserInfoVO>  getUserInfo() {
        return userService.getUserInfo();
    }

    @Operation(summary = "通过邮箱查询用户",description = "参数传邮箱")
    @PostMapping("/getUserInfoByEmail")
    public CurlResponse<UserByEmailVO>  getUserInfoByEmail(@RequestBody @Validated Param<String> param) {
        return userService.getUserInfoByEmail(param.getParam());
    }

    @Operation(summary = "上传头像")
    @PostMapping("/uploadAvatar")
    public CurlResponse<String> uploadAvatar(@RequestBody @Validated Param<String> param) {
        return null;
    }
}
