package org.example.chatflow.repository;

import org.example.chatflow.model.entity.EmojiPack;

import java.util.List;

/**
 * 表情包仓储接口
 */
public interface EmojiPackRepository extends BaseRepository<EmojiPack, Long> {

    List<EmojiPack> findAll();
}
