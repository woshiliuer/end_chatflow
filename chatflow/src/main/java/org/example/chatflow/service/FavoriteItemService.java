package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.favorite.FavoriteItemCollectFromMessageDTO;
import org.example.chatflow.model.vo.favorite.FavoriteItemPageVO;
import org.example.chatflow.model.vo.favorite.FavoriteItemDetailVO;

import java.util.List;

public interface FavoriteItemService {

    CurlResponse<Long> collectFromMessage(FavoriteItemCollectFromMessageDTO dto);

    CurlResponse<List<FavoriteItemPageVO>> list();

    CurlResponse<FavoriteItemDetailVO> detail(Long favoriteId);

    CurlResponse<Boolean> delete(Long favoriteId);
}
