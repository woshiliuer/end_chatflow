package org.example.chatflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.common.constants.FileSourceTypeConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.common.exception.BusinessException;
import org.example.chatflow.mapper.EmojiItemMapper;
import org.example.chatflow.model.dto.Emoji.AddEmojiFromMessageFileDTO;
import org.example.chatflow.model.dto.Emoji.CustomizeEmojiDTO;
import org.example.chatflow.model.dto.common.FileCommonDTO;
import org.example.chatflow.model.entity.*;
import org.example.chatflow.model.vo.Emoji.CustomizeEmojisVO;
import org.example.chatflow.model.vo.Emoji.EmojiItemListVO;
import org.example.chatflow.model.vo.Emoji.EmojiPackListVO;
import org.example.chatflow.model.vo.common.FileCommonVO;
import org.example.chatflow.repository.EmojiItemRepository;
import org.example.chatflow.repository.EmojiPackRepository;
import org.example.chatflow.repository.UserEmojiPackRepository;
import org.example.chatflow.repository.UserEmojiItemRepository;
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

    private final UserEmojiItemRepository userEmojiItemRepository;

    private Long getCurrentUserCustomizePackId() {
        Long userId = currentUserAccessor.getCurrentUser().getId();
        List<UserEmojiPack> userEmojiPacks = userEmojiPackRepository.findByUserId(userId);
        Set<Long> packIds = userEmojiPacks.stream().map(UserEmojiPack::getPackId).collect(Collectors.toSet());
        Map<Long, EmojiPack> emojiPackMap = emojiPackRepository.findPackMapByIds(packIds);
        for (UserEmojiPack userEmojiPack : userEmojiPacks) {
            EmojiPack emojiPack = emojiPackMap.get(userEmojiPack.getPackId());
            if (emojiPack != null && Integer.valueOf(2).equals(emojiPack.getType())) {
                return emojiPack.getId();
            }
        }
        return null;
    }

    private boolean isGifFile(FileCommonDTO file) {
        if (file == null) {
            return false;
        }
        String fileType = file.getFileType();
        if (fileType != null && fileType.toLowerCase().contains("gif")) {
            return true;
        }
        String fileName = file.getFileName();
        return fileName != null && fileName.toLowerCase().endsWith(".gif");
    }

    private String defaultEmojiName(FileCommonDTO file) {
        if (file == null || file.getFileName() == null) {
            return "表情";
        }
        String name = file.getFileName();
        int idx = name.lastIndexOf('.');
        if (idx > 0) {
            return name.substring(0, idx);
        }
        return name;
    }

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
        Map<Long, FileCommonVO> map = fileService.getBySourceMap(FileSourceTypeConstant.EMOJI_PACK_COVER, packIds);
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
    public CurlResponse<Void> unbindEmojiItem(Long param) {
        return null;
    }

    @Override
    public CurlResponse<Void> addCustomizeEmoji(CustomizeEmojiDTO dto) {
        Long customizePackId = getCurrentUserCustomizePackId();
        VerifyUtil.isTrue(customizePackId == null, ErrorCode.EMOJI_NOT_FOUND);

        EmojiItem emojiItem = new EmojiItem();
        emojiItem.setPackId(customizePackId);
        emojiItem.setName(dto.getName());
        emojiItem.setType(dto.getType());
        emojiItemRepository.save(emojiItem);

        FileCommonDTO file = dto.getFile();
        VerifyUtil.isTrue(file == null, ErrorCode.FILE_IS_NULL);
        file.setSourceId(emojiItem.getId());
        if (Integer.valueOf(2).equals(dto.getType())) {
            file.setSourceType(FileSourceTypeConstant.EMOJI_ITEM_STATIC);
        } else if (Integer.valueOf(3).equals(dto.getType())) {
            file.setSourceType(FileSourceTypeConstant.EMOJI_ITEM_GIF);
        }
        fileService.saveFile(file);

        return CurlResponse.success();
    }

    @Override
    public CurlResponse<Void> addEmojiFromMessageFile(AddEmojiFromMessageFileDTO dto) {
        Long customizePackId = getCurrentUserCustomizePackId();
        VerifyUtil.isTrue(customizePackId == null, ErrorCode.EMOJI_NOT_FOUND);

        FileCommonDTO file = dto.getFile();
        VerifyUtil.isTrue(file == null, ErrorCode.FILE_IS_NULL);

        Integer type = isGifFile(file) ? 3 : 2;

        EmojiItem emojiItem = new EmojiItem();
        emojiItem.setPackId(customizePackId);
        emojiItem.setName((dto.getName() == null || dto.getName().trim().isEmpty()) ? defaultEmojiName(file) : dto.getName().trim());
        emojiItem.setType(type);
        emojiItemRepository.save(emojiItem);

        file.setSourceId(emojiItem.getId());
        if (Integer.valueOf(2).equals(type)) {
            file.setSourceType(FileSourceTypeConstant.EMOJI_ITEM_STATIC);
        } else if (Integer.valueOf(3).equals(type)) {
            file.setSourceType(FileSourceTypeConstant.EMOJI_ITEM_GIF);
        }
        fileService.saveFile(file);

        return CurlResponse.success();
    }

    @Override
    public CurlResponse<List<CustomizeEmojisVO>> customizeEmojis() {
        Long customizePackId = getCurrentUserCustomizePackId();
        if (customizePackId == null) {
            return CurlResponse.success(Collections.emptyList());
        }

        List<EmojiItem> items = emojiItemRepository.findByPackId(customizePackId);
        Set<Long> itemIds = items.stream().map(EmojiItem::getId).collect(Collectors.toSet());
        Map<Long, FileCommonVO> gifFileMap = fileService.getBySourceMap(FileSourceTypeConstant.EMOJI_ITEM_GIF, itemIds);
        Map<Long, FileCommonVO> staticFileMap = fileService.getBySourceMap(FileSourceTypeConstant.EMOJI_ITEM_STATIC, itemIds);

        List<CustomizeEmojisVO> result = new ArrayList<>();
        for (EmojiItem emojiItem : items) {
            CustomizeEmojisVO vo = new CustomizeEmojisVO();
            vo.setId(emojiItem.getId());
            vo.setName(emojiItem.getName());
            vo.setType(emojiItem.getType());
            if (Integer.valueOf(2).equals(emojiItem.getType())) {
                vo.setFile(staticFileMap.get(emojiItem.getId()));
            } else if (Integer.valueOf(3).equals(emojiItem.getType())) {
                vo.setFile(gifFileMap.get(emojiItem.getId()));
            }
            result.add(vo);
        }
        return CurlResponse.success(result);
    }
}
