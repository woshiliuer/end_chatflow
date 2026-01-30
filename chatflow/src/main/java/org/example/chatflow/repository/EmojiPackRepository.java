package org.example.chatflow.repository;

import org.example.chatflow.model.entity.EmojiPack;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 表情包仓储接口
 */
public interface EmojiPackRepository extends BaseRepository<EmojiPack, Long> {

    List<EmojiPack> findAll();

    Map<Long, EmojiPack> findPackMapByIds(Set<Long> ids);

    List<EmojiPack> findPackByIds(Set<Long> ids);
}
