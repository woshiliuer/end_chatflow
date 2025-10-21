package org.example.chatflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.User.LoginDTO;
import org.example.chatflow.model.dto.User.RegisterDTO;
import org.example.chatflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author by zzr
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final UserService userService;


    @Operation(summary = "密码登录")
    @PostMapping("/login")
    public CurlResponse<String> login(@RequestBody @Validated  LoginDTO dto) {
        return userService.login(dto);
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public String register(@RequestBody RegisterDTO dto){
        return null;
    }

}
