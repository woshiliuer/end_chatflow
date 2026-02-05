package org.example.chatflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.entity.Param;
import org.example.chatflow.model.dto.favorite.FavoriteItemCollectFromMessageDTO;
import org.example.chatflow.model.vo.favorite.FavoriteItemPageVO;
import org.example.chatflow.model.vo.favorite.FavoriteItemDetailVO;
import org.example.chatflow.service.FavoriteItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/favoriteItem")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Tag(name = "用户收藏", description = "用户收藏项相关接口")
public class FavoriteItemController {

    private final FavoriteItemService favoriteItemService;

    @Operation(summary = "从消息收藏")
    @PostMapping("/collectFromMessage")
    public CurlResponse<Long> collectFromMessage(@RequestBody @Validated FavoriteItemCollectFromMessageDTO dto) {
        return favoriteItemService.collectFromMessage(dto);
    }

    @Operation(summary = "查询收藏列表")
    @PostMapping("/list")
    public CurlResponse<List<FavoriteItemPageVO>> list() {
        return favoriteItemService.list();
    }

    @Operation(summary = "查询收藏详情",description = "参数传收藏Id")
    @PostMapping("/detail")
    public CurlResponse<FavoriteItemDetailVO> detail(@RequestBody @Validated Param<Long> param) {
        return favoriteItemService.detail(param.getParam());
    }

    @Operation(summary = "删除收藏",description = "参数传收藏Id")
    @PostMapping("/delete")
    public CurlResponse<Boolean> delete(@RequestBody @Validated Param<Long> param) {
        return favoriteItemService.delete(param.getParam());
    }
}
