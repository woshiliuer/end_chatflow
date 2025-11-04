package org.example.chatflow.repository.impl;

import org.example.chatflow.common.enums.ChatGroupStatus;
import org.example.chatflow.common.enums.Deleted;
import org.example.chatflow.mapper.ChatGroupMapper;
import org.example.chatflow.model.entity.ChatGroup;
import org.example.chatflow.repository.ChatGroupRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 群聊仓储实现
 */
@Repository
public class ChatGroupRepositoryImpl
    extends BaseRepositoryImpl<ChatGroupMapper, ChatGroup, Long>
    implements ChatGroupRepository {

    @Override
    public List<ChatGroup> findNormalByIds(Set<Long> groupIds) {
        return lambdaQuery().in(ChatGroup::getId, groupIds)
                .eq(ChatGroup::getStatus, ChatGroupStatus.NORMAL.getCode())
                .eq(ChatGroup::getDeleted, Deleted.HAS_NOT_DELETED.getCode())
                .list();
    }

    @Override
    public ChatGroup findNormalById(Long groupId) {
        return lambdaQuery()
                .eq(ChatGroup::getId, groupId)
                .eq(ChatGroup::getStatus, ChatGroupStatus.NORMAL.getCode())
                .eq(ChatGroup::getDeleted, Deleted.HAS_NOT_DELETED.getCode())
                .one();
    }
}

