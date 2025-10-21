package org.example.chatflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.chatflow.model.dto.User.LoginDTO;
import org.example.chatflow.model.dto.User.RegisterDTO;
import org.springframework.web.bind.annotation.*;

/**
 * @author by zzr
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Operation(summary = "登录")
    @PostMapping("/login")
    public String login(@RequestBody LoginDTO dto) {
        return null;
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public String register(@RequestBody RegisterDTO dto){
        return null;
    }

}
