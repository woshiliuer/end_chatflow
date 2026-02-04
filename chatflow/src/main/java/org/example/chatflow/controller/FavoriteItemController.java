package org.example.chatflow.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/favoriteItem")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Tag(name = "用户收藏", description = "用户收藏项相关接口")
public class FavoriteItemController {
}
