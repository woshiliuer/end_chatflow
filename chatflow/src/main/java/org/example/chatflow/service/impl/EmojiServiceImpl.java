package org.example.chatflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.common.constants.FileSourceTypeConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.common.exception.BusinessException;
import org.example.chatflow.mapper.EmojiItemMapper;
import org.example.chatflow.model.dto.Emoji.CustomizeEmojiDTO;
import org.example.chatflow.model.dto.common.FileCommonDTO;
import org.example.chatflow.model.entity.*;
import org.example.chatflow.model.vo.Emoji.CustomizeEmojisVO;
import org.example.chatflow.model.vo.Emoji.EmojiItemListVO;
import org.example.chatflow.model.vo.Emoji.EmojiPackListVO;
import org.example.chatflow.model.vo.common.FileCommonVO;
import org.example.chatflow.repository.EmojiItemRepository;
import org.example.chatflow.repository.EmojiPackRepository;
import org.example.chatflow.repository.UserEmojiItemRepository;
import org.example.chatflow.repository.UserEmojiPackRepository;
import org.example.chatflow.service.*;
import org.example.chatflow.support.CurrentUserAccessor;
import org.example.chatflow.utils.AliOssUtil;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author by zzr
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmojiServiceImpl implements EmojiService {


    private final AliOssUtil aliOssUtil;

    private final EmojiPackRepository emojiPackRepository;

    private final FileService fileService;

    private final EmojiItemRepository emojiItemRepository;

    private final CurrentUserAccessor currentUserAccessor;

    private final UserEmojiPackRepository userEmojiPackRepository;

    private final UserEmojiItemRepository  userEmojiItemRepository;

    @Override
    public CurlResponse<List<EmojiPackListVO>> myEmojiPackList() {
        //获取当前用户Id
        User user = currentUserAccessor.getCurrentUser();
        //查询用户绑定的表情包
        List<UserEmojiPack> userEmojiPacks = userEmojiPackRepository.findByUserId(user.getId());
        //查询表情包详情
        Set<Long> packIds = userEmojiPacks.stream().map(
                UserEmojiPack::getPackId
        ).collect(Collectors.toSet());
        Map<Long, EmojiPack> emojiPackMap = emojiPackRepository.findPackMapByIds(packIds);
        //查询封面文件
        Map<Long, FileCommonVO> map = fileService.getBySourceMap(FileSourceTypeConstant.EMOJI_PACK_COVER,packIds);
        List<EmojiPackListVO> emojiPackListVOS = new ArrayList<>();
        for (UserEmojiPack userEmojiPack : userEmojiPacks) {
            EmojiPack emojiPack = emojiPackMap.get(userEmojiPack.getPackId());
            if (emojiPack != null) {
                EmojiPackListVO vo = EmojiPackListVO.EmojiPackListVOMapper.INSTANCE.toVO(emojiPack);
                vo.setCover(map.get(emojiPack.getId()));
                vo.setSort(userEmojiPack.getSort());
                emojiPackListVOS.add(vo);
            }
        }
        return CurlResponse.success(emojiPackListVOS);
    }

    @Override
    public CurlResponse<List<EmojiItemListVO>> emojiItems(Long param) {
        List<EmojiItem> emojiItems = emojiItemRepository.findByPackId(param);
        Set<Long> itemIds = emojiItems.stream().map(EmojiItem::getId).collect(Collectors.toSet());
        //查询表情包项文件
        Map<Long, FileCommonVO> gifFileMap = fileService.getBySourceMap(FileSourceTypeConstant.EMOJI_ITEM_GIF, itemIds);
        Map<Long, FileCommonVO> staticFileMap = fileService.getBySourceMap(FileSourceTypeConstant.EMOJI_ITEM_STATIC, itemIds);
        List<EmojiItemListVO> emojiItemListVOS = new ArrayList<>();
        for (EmojiItem emojiItem : emojiItems) {
            EmojiItemListVO vo = EmojiItemListVO.EmojiItemListVOMapper.INSTANCE.toVO(emojiItem);
            if (emojiItem != null && emojiItem.getId() != null) {
                if (Integer.valueOf(2).equals(emojiItem.getType())) {
                    vo.setEmojiItemFile(staticFileMap.get(emojiItem.getId()));
                } else if (Integer.valueOf(3).equals(emojiItem.getType())) {
                    vo.setEmojiItemFile(gifFileMap.get(emojiItem.getId()));
                }
            }
            emojiItemListVOS.add(vo);
        }
        return CurlResponse.success(emojiItemListVOS);
    }

    @Override
    public CurlResponse<EmojiPackListVO> emojiPackList() {
        return null;
    }

    @Override
    public CurlResponse<Void> bindEmojiPack(Long param) {
        return null;
    }

    @Override
    public CurlResponse<Void> unbindEmojiPack(Long param) {
        return null;
    }

    @Override
    public CurlResponse<Void> collectEmojiItem(Long param) {
        // 1. 验证表情包是否存在
        emojiItemRepository.findById(param)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMOJI_NOT_FOUND));
        //个人绑定和表情包关系
        Long currentUserId = currentUserAccessor.getCurrentUser().getId();
        Integer nestSort = userEmojiItemRepository.getNextSortValue(currentUserId);
        UserEmojiItem userEmojiItem = new UserEmojiItem();
        userEmojiItem.setUserId(currentUserId);
        userEmojiItem.setItemId(param);
        userEmojiItem.setSort(nestSort);
        userEmojiItemRepository.save(userEmojiItem);
        return CurlResponse.success();
    }

    @Override
    public CurlResponse<Void> unbindEmojiItem(Long param) {
        return null;
    }

    @Override
    public CurlResponse<Void> addCustomizeEmoji(CustomizeEmojiDTO dto) {
        EmojiItem emojiItem = new  EmojiItem();
        emojiItem.setName(dto.getName());
        emojiItem.setType(dto.getType());
        //保存表情包项
        emojiItemRepository.save(emojiItem);
        //保存文件
        dto.getFile().setSourceId(emojiItem.getId());
        fileService.saveFile(dto.getFile());
        //个人绑定和表情包关系
        Long currentUserId = currentUserAccessor.getCurrentUser().getId();
        Integer nestSort = userEmojiItemRepository.getNextSortValue(currentUserId);

        UserEmojiItem userEmojiItem = new UserEmojiItem();
        userEmojiItem.setUserId(currentUserId);
        userEmojiItem.setItemId(emojiItem.getId());
        userEmojiItem.setSort(nestSort);
        userEmojiItemRepository.save(userEmojiItem);
        return CurlResponse.success();
    }

    @Override
    public CurlResponse<List<CustomizeEmojisVO>> customizeEmojis() {
        Long currentUserId = currentUserAccessor.getCurrentUser().getId();
        List<UserEmojiItem> userEmojiItems = userEmojiItemRepository.findByUserId(currentUserId);
        Set<Long> itemIds = userEmojiItems.stream().map(UserEmojiItem::getItemId).collect(Collectors.toSet());
        List<EmojiItem> items = emojiItemRepository.findByIds(itemIds);
        List<CustomizeEmojisVO> customizeEmojisVOS = new ArrayList<>();
        Map<Long, FileCommonVO> fileMap = fileService.getBySourceMap(FileSourceTypeConstant.CUSTOMIZE_EMOJI,itemIds);
        for (EmojiItem emojiItem : items) {
            CustomizeEmojisVO vo = new  CustomizeEmojisVO();
            vo.setName(emojiItem.getName());
            vo.setType(emojiItem.getType());
            vo.setId(emojiItem.getId());
            FileCommonVO fileCommonVO = fileMap.get(emojiItem.getId());
            vo.setFile(fileCommonVO);
            customizeEmojisVOS.add(vo);
        }
        return CurlResponse.success(customizeEmojisVOS);
    }
}
