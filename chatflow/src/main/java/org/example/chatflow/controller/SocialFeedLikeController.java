package org.example.chatflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.entity.Param;
import org.example.chatflow.service.SocialFeedLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/socialFeedLike")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SocialFeedLikeController {

    private final SocialFeedLikeService socialFeedLikeService;

    @Operation(summary = "点赞")
    @PostMapping("/like")
    public CurlResponse<Boolean> like(@RequestBody @Validated Param<Long> param) {
        return socialFeedLikeService.like(param.getParam());
    }

    @Operation(summary = "取消点赞")
    @PostMapping("/unlike")
    public CurlResponse<Boolean> unlike(@RequestBody @Validated Param<Long> param) {
        return socialFeedLikeService.unlike(param.getParam());
    }
}
