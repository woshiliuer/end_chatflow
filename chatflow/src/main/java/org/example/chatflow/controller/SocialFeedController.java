package org.example.chatflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.entity.Param;
import org.example.chatflow.model.dto.social.SocialFeedListQueryDTO;
import org.example.chatflow.model.dto.social.SocialFeedPublishDTO;
import org.example.chatflow.model.vo.social.SocialFeedDetailVO;
import org.example.chatflow.model.vo.social.SocialFeedListTotalVO;
import org.example.chatflow.service.SocialFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/socialFeed")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SocialFeedController {

    private final SocialFeedService socialFeedService;

    @Operation(summary = "发布动态")
    @PostMapping("/publish")
    public CurlResponse<Long> publish(@RequestBody SocialFeedPublishDTO dto) {
        return socialFeedService.publish(dto);
    }

    @Operation(summary = "动态列表")
    @PostMapping("/list")
    public CurlResponse<SocialFeedListTotalVO> list(@RequestBody @Validated SocialFeedListQueryDTO dto) {
        return socialFeedService.list(dto);
    }

    @Operation(summary = "动态详情")
    @PostMapping("/detail")
    public CurlResponse<SocialFeedDetailVO> detail(@RequestBody @Validated Param<Long> param) {
        return socialFeedService.detail(param.getParam());
    }


}
