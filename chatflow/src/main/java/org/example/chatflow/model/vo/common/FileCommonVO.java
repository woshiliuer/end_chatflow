package org.example.chatflow.model.vo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.entity.FileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;


@Data
@Schema(description = "文件公共VO")
public class FileCommonVO {
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

    @Schema(description = "完整路径")
    private String fullFilePath;

    @Mapper
    public interface FileCommonVOMapper{
        FileCommonVOMapper INSTANCE = Mappers.getMapper(FileCommonVOMapper.class);
        FileCommonVO toVO(FileEntity file,String fullFilePath);
    }
}
