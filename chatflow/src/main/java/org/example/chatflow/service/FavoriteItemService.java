package org.example.chatflow.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.favorite.FavoriteItemCreateDTO;
import org.example.chatflow.model.dto.favorite.FavoriteItemPageQueryDTO;
import org.example.chatflow.model.vo.favorite.FavoriteItemVO;

public interface FavoriteItemService {

    CurlResponse<Long> create(FavoriteItemCreateDTO dto);

    CurlResponse<Page<FavoriteItemVO>> page(FavoriteItemPageQueryDTO dto);

    CurlResponse<Boolean> delete(Long id);
}
