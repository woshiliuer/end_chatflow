package org.example.chatflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.chatflow.common.constants.FileSourceTypeConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.model.entity.FileEntity;
import org.example.chatflow.model.vo.common.FileCommonVO;
import org.example.chatflow.utils.AliOssUtil;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileController {

    private final AliOssUtil aliOssUtil;

    @Operation(summary = "上传文件，消息相关文件")
    @PostMapping("/message/upload")
    public CurlResponse<FileCommonVO> messageUpload(@RequestParam("file") MultipartFile file) {
        VerifyUtil.isTrue(file == null || file.isEmpty(), ErrorCode.FILE_IS_NULL);

        String objectName = AliOssUtil.buildFileName("message", file.getOriginalFilename());
        String url = aliOssUtil.upload(file, objectName);
        String objectKey = AliOssUtil.toObjectKey(url);

        FileEntity entity = new FileEntity();
        entity.setSourceType(FileSourceTypeConstant.MESSAGE_FILE);
        entity.setSourceId(null);
        entity.setFileType(extractFileType(file.getOriginalFilename()));
        entity.setFileName(file.getOriginalFilename());
        entity.setFileSize(file.getSize());
        entity.setFilePath(objectKey);
        entity.setFileDesc(null);

        FileCommonVO vo = FileCommonVO.FileCommonVOMapper.INSTANCE.toVO(entity, url);
        return CurlResponse.success(vo);
    }

    @Operation(summary = "上传文件，自定义表情包")
    @PostMapping("/customizeEmoji/upload")
    public CurlResponse<FileCommonVO> customizeEmojiUpload(@RequestParam("file") MultipartFile file) {
        VerifyUtil.isTrue(file == null || file.isEmpty(), ErrorCode.FILE_IS_NULL);

        String objectName = AliOssUtil.buildFileName("emoji/customize", file.getOriginalFilename());
        String url = aliOssUtil.upload(file, objectName);
        String objectKey = AliOssUtil.toObjectKey(url);

        FileEntity entity = new FileEntity();
        entity.setSourceType(FileSourceTypeConstant.CUSTOMIZE_EMOJI);
        entity.setSourceId(null);
        entity.setFileType(extractFileType(file.getOriginalFilename()));
        entity.setFileName(file.getOriginalFilename());
        entity.setFileSize(file.getSize());
        entity.setFilePath(objectKey);
        entity.setFileDesc(null);

        FileCommonVO vo = FileCommonVO.FileCommonVOMapper.INSTANCE.toVO(entity, url);
        return CurlResponse.success(vo);
    }

    private String extractFileType(String originalFilename) {
        String filename = StringUtils.trimToEmpty(originalFilename);
        if (!filename.contains(".")) {
            return null;
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
