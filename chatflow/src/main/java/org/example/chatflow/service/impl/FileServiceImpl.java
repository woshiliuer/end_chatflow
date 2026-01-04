package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.constants.OssConstant;
import org.example.chatflow.model.dto.common.FileCommonDTO;
import org.example.chatflow.model.entity.FileEntity;
import org.example.chatflow.repository.FileRepository;
import org.example.chatflow.service.FileService;
import org.example.chatflow.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Override
    public boolean saveFile(FileCommonDTO dto) {
        if (dto == null) {
            return false;
        }
        String sourceType = dto.getSourceType();
        Long sourceId = dto.getSourceId();
        if (sourceType == null || sourceType.isBlank() || sourceId == null) {
            return false;
        }
        FileEntity entity = FileCommonDTO.FileCommonDTOMapper.INSTANCE
                .toEntity(dto);
        return fileRepository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFile(FileCommonDTO dto) {
        if (dto == null) {
            return false;
        }
        String sourceType = dto.getSourceType();
        Long sourceId = dto.getSourceId();
        if (sourceType == null || sourceType.isBlank() || sourceId == null) {
            return false;
        }
        fileRepository.deleteById(sourceId);
        return fileRepository.save(FileCommonDTO.FileCommonDTOMapper.INSTANCE.toEntity(dto));
    }

    @Override
    public boolean deleteFile(String sourceType, Long sourceId) {
        if (sourceType == null || sourceType.isBlank() || sourceId == null) {
            return false;
        }
        return fileRepository.deleteBySource(sourceType,sourceId);
    }

    @Override
    public boolean saveBatchFile(List<FileCommonDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return false;
        }
        List<FileEntity> fileEntities = FileCommonDTO.FileCommonDTOMapper.INSTANCE.toEntitys(dtos);
        return fileRepository.saveBatch(fileEntities);
    }
}
