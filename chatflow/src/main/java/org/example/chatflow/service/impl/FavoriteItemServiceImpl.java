package org.example.chatflow.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.model.dto.favorite.FavoriteItemCreateDTO;
import org.example.chatflow.model.dto.favorite.FavoriteItemPageQueryDTO;
import org.example.chatflow.model.entity.FavoriteItem;
import org.example.chatflow.model.vo.favorite.FavoriteItemVO;
import org.example.chatflow.repository.FavoriteItemRepository;
import org.example.chatflow.service.FavoriteItemService;
import org.example.chatflow.support.CurrentUserAccessor;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FavoriteItemServiceImpl implements FavoriteItemService {

    private final FavoriteItemRepository favoriteItemRepository;
    private final CurrentUserAccessor currentUserAccessor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<Long> create(FavoriteItemCreateDTO dto) {
        VerifyUtil.isTrue(dto == null, ErrorCode.VALIDATION_ERROR);
        VerifyUtil.isTrue(dto.getItemType() == null, ErrorCode.VALIDATION_ERROR);
        VerifyUtil.isTrue(dto.getSourceType() == null, ErrorCode.VALIDATION_ERROR);

        Long currentUserId = currentUserAccessor.getCurrentUser().getId();
        VerifyUtil.isTrue(currentUserId == null, ErrorCode.USER_NOT_LOGIN);

        FavoriteItem entity = new FavoriteItem();
        entity.setUserId(currentUserId);
        entity.setItemType(dto.getItemType());
        entity.setTextContent(dto.getTextContent());
        entity.setSourceType(dto.getSourceType());
        entity.setSenderId(dto.getSenderId());
        entity.setSenderName(dto.getSenderName());
        entity.setSendTime(dto.getSendTime());
        entity.setGroupId(dto.getGroupId());
        entity.setGroupName(dto.getGroupName());

        VerifyUtil.ensureOperationSucceeded(favoriteItemRepository.save(entity), ErrorCode.INTERNAL_ERROR);
        return CurlResponse.success(entity.getId());
    }

    @Override
    public CurlResponse<Page<FavoriteItemVO>> page(FavoriteItemPageQueryDTO dto) {
        VerifyUtil.isTrue(dto == null, ErrorCode.VALIDATION_ERROR);

        Long currentUserId = currentUserAccessor.getCurrentUser().getId();
        VerifyUtil.isTrue(currentUserId == null, ErrorCode.USER_NOT_LOGIN);

        int page = dto.getPage() == null ? 1 : dto.getPage();
        int size = dto.getSize() == null ? 20 : dto.getSize();

        Page<FavoriteItem> p = favoriteItemRepository.pageByUserId(currentUserId, page, size, dto.getItemType());
        Page<FavoriteItemVO> voPage = new Page<>(p.getCurrent(), p.getSize(), p.getTotal());
        voPage.setRecords(p.getRecords().stream().map(FavoriteItemVO::fromEntity).collect(Collectors.toList()));
        return CurlResponse.success(voPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<Boolean> delete(Long id) {
        VerifyUtil.isTrue(id == null, ErrorCode.VALIDATION_ERROR);

        Long currentUserId = currentUserAccessor.getCurrentUser().getId();
        VerifyUtil.isTrue(currentUserId == null, ErrorCode.USER_NOT_LOGIN);

        FavoriteItem entity = favoriteItemRepository.findById(id).orElse(null);
        VerifyUtil.isTrue(entity == null, ErrorCode.BUSINESS_ERROR);
        VerifyUtil.isTrue(!currentUserId.equals(entity.getUserId()), ErrorCode.BUSINESS_ERROR);

        VerifyUtil.ensureOperationSucceeded(favoriteItemRepository.deleteById(id), ErrorCode.INTERNAL_ERROR);
        return CurlResponse.success(true);
    }
}
