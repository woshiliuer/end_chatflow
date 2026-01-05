package org.example.chatflow.repository;

import org.example.chatflow.model.entity.FileEntity;

import java.util.Collection;
import java.util.List;

public interface FileRepository extends BaseRepository<FileEntity, Long> {

    FileEntity findLatestBySource(String sourceType, Long sourceId);

    List<FileEntity> findBySource(String sourceType, Long sourceId);

    List<FileEntity> findLatestBySourceIds(String sourceType, Collection<Long> sourceIds);

    boolean deleteBySource(String sourceType, Long sourceId);
}
