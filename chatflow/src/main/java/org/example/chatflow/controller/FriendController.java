package org.example.chatflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author by zzr
 */
@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FriendController {

//    @Operation(summary = "获取用户对应的好友")
//    @PostMapping()
}
