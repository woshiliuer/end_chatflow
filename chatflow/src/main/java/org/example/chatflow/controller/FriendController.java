package org.example.chatflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.entity.Param;
import org.example.chatflow.model.vo.GetFriendListVO;
import org.example.chatflow.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author by zzr
 */
@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "获取用户对应的好友")
    @PostMapping("/getFriends")
    public CurlResponse<List<GetFriendListVO>> getFriends() {
        return friendService.getFriends();
    }

    @Operation(summary = "申请添加好友" ,description = "参数传好友的用户Id")
    @PostMapping("/addFriendRequest")
    public CurlResponse<String> addFriend(@RequestBody @Validated Param<Long> param){
        return friendService.addFriendRequest(param.getParam());
    }
}
