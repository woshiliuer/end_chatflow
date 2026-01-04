package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.common.constants.FileSourceTypeConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.Emoji.EmojiItemDTO;
import org.example.chatflow.model.dto.Emoji.EmojiPackUploadDTO;
import org.example.chatflow.model.dto.common.FileCommonDTO;
import org.example.chatflow.model.entity.EmojiItem;
import org.example.chatflow.model.entity.EmojiPack;
import org.example.chatflow.service.EmojiItemService;
import org.example.chatflow.service.EmojiPackService;
import org.example.chatflow.service.EmojiService;
import org.example.chatflow.service.FileService;
import org.example.chatflow.utils.AliOssUtil;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author by zzr
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmojiServiceImpl implements EmojiService {

    private final AliOssUtil aliOssUtil;

    private final EmojiPackService emojiPackService;

    private final FileService fileService;

    private final EmojiItemService emojiItemService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<Void> uploadEmojiPack(EmojiPackUploadDTO emojiPackUploadDTO) {

        try {
            //保存表情包
            EmojiPack emojiPack = new  EmojiPack();
            emojiPack.setName(emojiPackUploadDTO.getName());
            VerifyUtil.ensureOperationSucceeded(emojiPackService.save(emojiPack),"保存表情包失败");
            //上传封面到OSS
            MultipartFile coverFile =  emojiPackUploadDTO.getCoverImage();
            String coverUrl = aliOssUtil.upload(coverFile.getBytes(), aliOssUtil.buildFileName(
                    coverFile,emojiPack.getId()
            ));
            //保存文件
            String coverPath = aliOssUtil.toObjectKey(coverUrl);
            fileService.saveFile(FileCommonDTO.FileCommonDTOMapper.INSTANCE.toDTO(
                    FileSourceTypeConstant.EMOJI_PACK_COVER,
                    emojiPack.getId(),
                    coverFile.getContentType(),
                    coverFile.getOriginalFilename(),
                    coverFile.getSize(),
                    coverPath,
                    null
            ));

            //保存表情项
            List<EmojiItem> emojiItems = new ArrayList<>();
            List<FileCommonDTO> fileDTOs = new ArrayList<>();
            for (EmojiItemDTO itemDTO : emojiPackUploadDTO.getEmojiItems()) {
                EmojiItem emojiItem = EmojiItemDTO.EmojiItemDTOMapper.INSTANCE.toEntity(itemDTO);
                emojiItem.setPackId(emojiPack.getId());
                emojiItems.add(emojiItem);
            }
            emojiItemService.saveBatch(emojiItems);

            // 处理文件上传和保存
            for (int i = 0; i < emojiPackUploadDTO.getEmojiItems().size(); i++) {
                EmojiItemDTO itemDTO = emojiPackUploadDTO.getEmojiItems().get(i);
                EmojiItem emojiItem = emojiItems.get(i);
                MultipartFile emojiFile = itemDTO.getFile();

                // 上传表情到OSS
                String emojiUrl = aliOssUtil.upload(
                        emojiFile.getBytes(),
                        aliOssUtil.buildFileName(emojiFile, emojiItem.getId())
                );
                // 准备文件DTO
                String emojiPath = aliOssUtil.toObjectKey(emojiUrl);
                fileDTOs.add(FileCommonDTO.FileCommonDTOMapper.INSTANCE.toDTO(
                        FileSourceTypeConstant.EMOJI_ITEM,
                        emojiItem.getId(),
                        emojiFile.getContentType(),
                        emojiFile.getOriginalFilename(),
                        emojiFile.getSize(),
                        emojiPath,
                        null
                ));
            }
            // 保存文件
            fileService.saveBatchFile(fileDTOs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }


}
