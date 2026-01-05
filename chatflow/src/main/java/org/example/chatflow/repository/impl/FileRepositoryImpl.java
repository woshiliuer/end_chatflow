package org.example.chatflow.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.chatflow.mapper.FileMapper;
import org.example.chatflow.model.entity.FileEntity;
import org.example.chatflow.repository.FileRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    public List<FileEntity> findLatestBySourceIds(String sourceType, Collection<Long> sourceIds) {
        if (sourceType == null || sourceType.isBlank() || sourceIds == null || sourceIds.isEmpty()) {
            return Collections.emptyList();
        }

        QueryWrapper<FileEntity> qw = new QueryWrapper<>();
        qw.select("source_id", "MAX(id) AS id")
                .eq("deleted", 0)
                .eq("source_type", sourceType)
                .in("source_id", sourceIds)
                .groupBy("source_id");

        List<Map<String, Object>> rows = baseMapper.selectMaps(qw);
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> maxIds = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            Object idObj = row.get("id");
            if (idObj instanceof Number number) {
                maxIds.add(number.longValue());
            }
        }
        if (maxIds.isEmpty()) {
            return Collections.emptyList();
        }

        return listByIds(maxIds);
    }

    @Override
    public boolean deleteBySource(String sourceType, Long sourceId) {
        if (sourceType == null || sourceType.isBlank() || sourceId == null) {
            return false;
        }
        return lambdaUpdate()
                .eq(FileEntity::getSourceType, sourceType)
                .eq(FileEntity::getSourceId, sourceId)
                .set(FileEntity::getDeleted, 1)
                .update();
    }
}
