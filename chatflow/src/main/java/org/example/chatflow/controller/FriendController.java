package org.example.chatflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.entity.Param;
import org.example.chatflow.model.dto.friend.AddRequestDTO;
import org.example.chatflow.model.dto.friend.AgreeRequestDTO;
import org.example.chatflow.model.vo.FriendRequestListTotalVO;
import org.example.chatflow.model.vo.FriendRequestListVO;
import org.example.chatflow.model.vo.GetFriendListVO;
import org.example.chatflow.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "申请添加好友")
    @PostMapping("/addFriendRequest")
    public CurlResponse<String> addFriend(@RequestBody @Validated AddRequestDTO dto) {
        return friendService.addFriendRequest(dto);
    }

    @Operation(summary = "好友申请列表")
    @GetMapping("/friendRequestList")
    public CurlResponse<FriendRequestListTotalVO> friendRequestList(){
        return friendService.friendRequestList();
    }

    @Operation(summary = "同意好友申请",description = "参数传好友Id")
    @PostMapping("/agreeFriendRequest")
    public CurlResponse<String> agreeFriendRequest(@RequestBody @Validated AgreeRequestDTO dto) {
        return friendService.agreeFriendRequest(dto);
    }

    @Operation(summary = "拒绝好友申请",description = "参数传好友Id")
    @PostMapping("/disagreeFriendRequest")
    public CurlResponse<String> disagreeFriendRequest(@RequestBody @Validated Param<Long> param) {
        return friendService.disagreeFriendRequest(param.getParam());
    }
}
