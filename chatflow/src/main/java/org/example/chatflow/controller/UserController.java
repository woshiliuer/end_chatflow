package org.example.chatflow.controller;

import org.example.chatflow.model.dto.User.LoginDTO;
import org.springframework.web.bind.annotation.*;

/**
 * @author by zzr
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO dto) {
        return null;
    }
}
