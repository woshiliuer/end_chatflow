package org.example.chatflow.model.dto.common;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.entity.FileEntity;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "文件公共DTO")
public class FileCommonDTO {
    @Schema(description = "数据来源类型")
    private String sourceType;

    @Schema(description = "数据来源ID")
    private Long sourceId;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "文件说明")
    private String fileDesc;

    @Mapper
    public interface FileCommonDTOMapper {
        FileCommonDTOMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(FileCommonDTOMapper.class);

        FileCommonDTO toDTO(
                String sourceType,
                Long sourceId,
                String fileType,
                String fileName,
                Long fileSize,
                String filePath,
                String fileDesc
        );

        FileEntity toEntity(FileCommonDTO dto);

        List<FileEntity> toEntitys(List<FileCommonDTO> dtos);
    }
}
