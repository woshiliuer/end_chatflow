package org.example.chatflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.entity.Param;
import org.example.chatflow.model.dto.social.SocialFeedCommentDTO;
import org.example.chatflow.service.SocialFeedCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/socialFeedComment")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SocialFeedCommentController {

    private final SocialFeedCommentService socialFeedCommentService;

    @Operation(summary = "对动态评论")
    @PostMapping("/comment")
    public CurlResponse<Long> comment(@RequestBody @Validated SocialFeedCommentDTO dto) {
        return socialFeedCommentService.comment(dto.getFeedId(), dto.getContent());
    }

    @Operation(summary = "删除自己的评论")
    @PostMapping("/delete")
    public CurlResponse<Boolean> delete(@RequestBody @Validated Param<Long> param) {
        return socialFeedCommentService.deleteMyComment(param.getParam());
    }
}
