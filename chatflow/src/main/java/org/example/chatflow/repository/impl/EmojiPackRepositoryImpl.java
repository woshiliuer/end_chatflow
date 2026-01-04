package org.example.chatflow.repository.impl;

import org.example.chatflow.mapper.EmojiPackMapper;
import org.example.chatflow.model.entity.EmojiPack;
import org.example.chatflow.repository.EmojiPackRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
