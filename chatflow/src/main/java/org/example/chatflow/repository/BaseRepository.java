package org.example.chatflow.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 通用仓储接口，抽象常见 CRUD 能力。
 *
 * @param <T>  实体类型
 * @param <ID> 主键类型
 */
public interface BaseRepository<T, ID extends Serializable> {

    /**
     * 根据主键查询实体。
     *
     * @param id 主键
     * @return 查询结果
     */
    Optional<T> findById(ID id);

    /**
     * 根据主键集合查询实体列表。
     *
     * @param ids 主键集合
     * @return 实体列表
     */
    List<T> findByIds(Collection<ID> ids);

    /**
     * 判断主键对应的记录是否存在。
     *
     * @param id 主键
     * @return true 表示存在
     */
    boolean existsById(ID id);

    /**
     * 保存单个实体。
     *
     * @param entity 实体对象
     * @return 是否保存成功
     */
    boolean save(T entity);

    /**
     * 批量保存实体。
     *
     * @param entities 实体集合
     * @return 是否保存成功
     */
    boolean saveBatch(Collection<T> entities);

    /**
     * 更新实体。
     *
     * @param entity 实体对象
     * @return 是否更新成功
     */
    boolean update(T entity);

    /**
     * 根据主键删除实体。
     *
     * @param id 主键
     * @return 是否删除成功
     */
    boolean deleteById(ID id);

    /**
     * 批量删除实体。
     *
     * @param ids 主键集合
     * @return 是否删除成功
     */
    boolean deleteByIds(Collection<ID> ids);
}
