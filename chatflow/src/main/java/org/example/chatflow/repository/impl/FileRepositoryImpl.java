package org.example.chatflow.repository.impl;

import org.example.chatflow.mapper.FileMapper;
import org.example.chatflow.model.entity.FileEntity;
import org.example.chatflow.repository.FileRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class FileRepositoryImpl extends BaseRepositoryImpl<FileMapper, FileEntity, Long> implements FileRepository {

    @Override
    public FileEntity findLatestBySource(String sourceType, Long sourceId) {
        if (sourceType == null || sourceType.isBlank() || sourceId == null) {
            return null;
        }
        return lambdaQuery()
                .eq(FileEntity::getSourceType, sourceType)
                .eq(FileEntity::getSourceId, sourceId)
                .orderByDesc(FileEntity::getId)
                .last("limit 1")
                .one();
    }

    @Override
    public List<FileEntity> findBySource(String sourceType, Long sourceId) {
        if (sourceType == null || sourceType.isBlank() || sourceId == null) {
            return Collections.emptyList();
        }
        return lambdaQuery()
                .eq(FileEntity::getSourceType, sourceType)
                .eq(FileEntity::getSourceId, sourceId)
                .orderByDesc(FileEntity::getId)
                .list();
    }

    @Override
    public boolean deleteBySource(String sourceType, Long sourceId) {
        return remove(lambdaQuery()
                .eq(FileEntity::getSourceType, sourceType)
                .eq(FileEntity::getSourceId, sourceId));
    }
}
