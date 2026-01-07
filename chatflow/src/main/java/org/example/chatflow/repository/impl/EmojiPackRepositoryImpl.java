package org.example.chatflow.repository.impl;

import org.example.chatflow.mapper.EmojiPackMapper;
import org.example.chatflow.model.entity.EmojiPack;
import org.example.chatflow.repository.EmojiPackRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表情包仓储实现
 */
@Repository
public class EmojiPackRepositoryImpl
    extends BaseRepositoryImpl<EmojiPackMapper, EmojiPack, Long>
    implements EmojiPackRepository {

    @Override
    public List<EmojiPack> findAll() {
        return lambdaQuery().list();
    }

    @Override
    public Map<Long, EmojiPack> findPackByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        List<EmojiPack> emojiPacks = this.findByIds(ids.stream().toList());
        if (emojiPacks.isEmpty()) {
            return Collections.emptyMap();
        }
        return emojiPacks.stream().collect(Collectors.toMap((e) -> e.getId(), (e) -> e));
    }
}
