package org.example.chatflow.service;

import org.example.chatflow.model.dto.common.FileCommonDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    boolean saveFile(FileCommonDTO fileCommonDTO);

    boolean updateFile(FileCommonDTO dto);

    boolean deleteFile(String sourceType,Long sourceId);

    boolean saveBatchFile(List<FileCommonDTO> fileDTOs);
}
