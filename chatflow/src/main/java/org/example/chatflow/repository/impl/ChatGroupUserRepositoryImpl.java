package org.example.chatflow.repository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.example.chatflow.mapper.ChatGroupUserMapper;
import org.example.chatflow.model.entity.ChatGroupUser;
import org.example.chatflow.repository.ChatGroupUserRepository;
import org.springframework.stereotype.Repository;

/**
 * 群聊成员关系仓储实现
 */
@Repository
public class ChatGroupUserRepositoryImpl
    extends BaseRepositoryImpl<ChatGroupUserMapper, ChatGroupUser, Long>
    implements ChatGroupUserRepository {

    @Override
    public List<ChatGroupUser> findByGroupId(Long groupId) {
        if (groupId == null) {
            return Collections.emptyList();
        }
        return lambdaQuery()
            .eq(ChatGroupUser::getGroupId, groupId)
            .list();
    }

    @Override
    public List<ChatGroupUser> findByGroupIds(Set<Long> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) {
            return Collections.emptyList();
        }
        return lambdaQuery()
            .in(ChatGroupUser::getGroupId, groupIds)
            .list();
    }

    @Override
    public List<ChatGroupUser> findByMemberId(Long memberId) {
        if (memberId == null) {
            return Collections.emptyList();
        }
        return lambdaQuery()
            .eq(ChatGroupUser::getMemberId, memberId)
            .list();
    }
}
