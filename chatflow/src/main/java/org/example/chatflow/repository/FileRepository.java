package org.example.chatflow.repository;

import org.example.chatflow.model.entity.FileEntity;

import java.util.List;

public interface FileRepository extends BaseRepository<FileEntity, Long> {

    FileEntity findLatestBySource(String sourceType, Long sourceId);

    List<FileEntity> findBySource(String sourceType, Long sourceId);

    boolean deleteBySource(String sourceType, Long sourceId);
}
