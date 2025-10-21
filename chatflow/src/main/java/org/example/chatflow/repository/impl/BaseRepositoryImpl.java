package org.example.chatflow.repository.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.chatflow.repository.BaseRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepositoryImpl<M extends BaseMapper<T>, T, ID extends Serializable>
    extends ServiceImpl<M, T> implements BaseRepository<T, ID> {

    @Override
    public Optional<T> findById(ID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(getById(id));
    }

    @Override
    public List<T> findByIds(Collection<ID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(listByIds(ids));
    }

    @Override
    public boolean existsById(ID id) {
        if (id == null) {
            return false;
        }
        return getById(id) != null;
    }

    @Override
    public boolean save(T entity) {
        if (entity == null) {
            return false;
        }
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return false;
        }
        return super.saveBatch(entities);
    }

    @Override
    public boolean update(T entity) {
        if (entity == null) {
            return false;
        }
        return updateById(entity);
    }

    @Override
    public boolean deleteById(ID id) {
        if (id == null) {
            return false;
        }
        return removeById(id);
    }

    @Override
    public boolean deleteByIds(Collection<ID> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return removeByIds(ids);
    }
}
