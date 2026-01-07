package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.common.constants.FileSourceTypeConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.Emoji.CustomizeEmojiDTO;
import org.example.chatflow.model.entity.EmojiPack;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.model.entity.UserEmojiPack;
import org.example.chatflow.model.vo.Emoji.EmojiItemListVO;
import org.example.chatflow.model.vo.Emoji.EmojiPackListVO;
import org.example.chatflow.model.vo.common.FileCommonVO;
import org.example.chatflow.repository.EmojiPackRepository;
import org.example.chatflow.repository.UserEmojiPackRepository;
import org.example.chatflow.service.*;
import org.example.chatflow.support.CurrentUserAccessor;
import org.example.chatflow.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private final EmojiItemService emojiItemService;

    private final CurrentUserAccessor currentUserAccessor;

    private final UserEmojiPackRepository userEmojiPackRepository;

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
        Map<Long,EmojiPack> emojiPacks = emojiPackRepository.findPackByIds(packIds);
        //查询封面文件
        Map<Long, FileCommonVO> map = fileService.getBySourceMap(FileSourceTypeConstant.EMOJI_PACK_COVER,packIds);

        return null;
    }

    @Override
    public CurlResponse<List<EmojiItemListVO>> emojiItems(Long param) {
        return null;
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
        return null;
    }

    @Override
    public CurlResponse<Void> unbindEmojiItem(Long param) {
        return null;
    }

    @Override
    public CurlResponse<Void> customizeEmoji(CustomizeEmojiDTO dto) {
        return null;
    }

    @Override
    public CurlResponse<Void> uploadEmoji(MultipartFile file) {
        return null;
    }
}
