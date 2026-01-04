package org.example.chatflow.repository;

import org.example.chatflow.model.entity.UserEmojiPack;

import java.util.List;

/**
 * 用户-表情包关系仓储接口
 */
public interface UserEmojiPackRepository extends BaseRepository<UserEmojiPack, Long> {

    List<UserEmojiPack> findByUserId(Long userId);
}
