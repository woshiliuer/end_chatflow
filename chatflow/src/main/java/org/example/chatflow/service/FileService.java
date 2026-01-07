package org.example.chatflow.service;

import org.example.chatflow.model.dto.common.FileCommonDTO;
import org.example.chatflow.model.vo.common.FileCommonVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FileService {

    String getLatestFilePath(String sourceType, Long sourceId);

    String getLatestFullUrl(String sourceType, Long sourceId, String defaultFilePath);

    Map<Long, String> getLatestFilePathMap(String sourceType, Collection<Long> sourceIds);

    Map<Long, String> getLatestFullUrlMap(String sourceType, Collection<Long> sourceIds, String defaultFilePath);

    Map<Long, FileCommonVO> getBySourceMap(String sourceType, Collection<Long> sourceIds);

    boolean saveFile(FileCommonDTO fileCommonDTO);

    boolean updateFile(FileCommonDTO dto);

    boolean deleteFile(String sourceType,Long sourceId);

    boolean saveBatchFile(List<FileCommonDTO> fileDTOs);
}
