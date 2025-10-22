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
import org.example.chatflow.service.UserService;
import org.example.chatflow.utils.JwtUtil;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
