package org.example.chatflow.repository;

import org.example.chatflow.model.entity.ChatGroup;

import java.util.List;
import java.util.Set;

/**
 * 群聊仓储接口
 */
public interface ChatGroupRepository extends BaseRepository<ChatGroup, Long> {
    List<ChatGroup> findNormalByIds(Set<Long> groupIds);

    ChatGroup findNormalById(Long groupId);
}

