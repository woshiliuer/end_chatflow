package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.constants.OssConstant;
import org.example.chatflow.model.dto.common.FileCommonDTO;
import org.example.chatflow.model.entity.FileEntity;
import org.example.chatflow.model.vo.common.FileCommonVO;
import org.example.chatflow.repository.FileRepository;
import org.example.chatflow.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Override
    public String getLatestFilePath(String sourceType, Long sourceId) {
        FileEntity latest = fileRepository.findLatestBySource(sourceType, sourceId);
        return latest == null ? null : latest.getFilePath();
    }

    @Override
    public String getLatestFullUrl(String sourceType, Long sourceId, String defaultFilePath) {
        String path = getLatestFilePath(sourceType, sourceId);
        if (path == null || path.isBlank()) {
            if (defaultFilePath == null || defaultFilePath.isBlank()) {
                return null;
            }
            return OssConstant.buildFullUrl(defaultFilePath);
        }
        return OssConstant.buildFullUrl(path);
    }

    @Override
    public Map<Long, String> getLatestFilePathMap(String sourceType, Collection<Long> sourceIds) {
        if (sourceType == null || sourceType.isBlank() || sourceIds == null || sourceIds.isEmpty()) {
            return new LinkedHashMap<>();
        }
        Map<Long, String> result = new LinkedHashMap<>();
        List<FileEntity> latestList = fileRepository.findLatestBySourceIds(sourceType, sourceIds);
        if (latestList == null || latestList.isEmpty()) {
            return result;
        }
        for (FileEntity entity : latestList) {
            if (entity == null || entity.getSourceId() == null) {
                continue;
            }
            result.putIfAbsent(entity.getSourceId(), entity.getFilePath());
        }
        return result;
    }

    @Override
    public Map<Long, String> getLatestFullUrlMap(String sourceType, Collection<Long> sourceIds, String defaultFilePath) {
        Map<Long, String> pathMap = getLatestFilePathMap(sourceType, sourceIds);
        Map<Long, String> result = new LinkedHashMap<>();
        if (sourceIds == null || sourceIds.isEmpty()) {
            return result;
        }
        for (Long id : sourceIds) {
            String path = pathMap.get(id);
            if (path == null || path.isBlank()) {
                result.put(id, defaultFilePath == null ? null : OssConstant.buildFullUrl(defaultFilePath));
            } else {
                result.put(id, OssConstant.buildFullUrl(path));
            }
        }
        return result;
    }

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
        fileRepository.deleteBySource(sourceType, sourceId);
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

    @Override
    public Map<Long, FileCommonVO> getBySourceMap(String sourceType, Collection<Long> sourceIds) {
        if (sourceType == null || sourceType.isBlank() || sourceIds == null || sourceIds.isEmpty()) {
            return new LinkedHashMap<>();
        }
        Map<Long, FileCommonVO> result = new LinkedHashMap<>();
        List<FileEntity> latestList = fileRepository.findLatestBySourceIds(sourceType, sourceIds);
        if (latestList == null || latestList.isEmpty()) {
            return result;
        }
        for (FileEntity entity : latestList) {
            if (entity == null || entity.getSourceId() == null) {
                continue;
            }
            FileCommonVO vo = FileCommonVO.FileCommonVOMapper.INSTANCE.toVO(entity,OssConstant.buildFullUrl(entity.getFilePath()));
            result.putIfAbsent(entity.getSourceId(), vo);
        }
        return result;
    }

    @Override
    public List<FileCommonVO> listBySource(String sourceType, Long sourceId) {
        List<FileEntity> entities = fileRepository.findBySource(sourceType, sourceId);
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
            .map(e -> FileCommonVO.FileCommonVOMapper.INSTANCE.toVO(e, OssConstant.buildFullUrl(e.getFilePath())))
            .collect(Collectors.toList());
    }
}
