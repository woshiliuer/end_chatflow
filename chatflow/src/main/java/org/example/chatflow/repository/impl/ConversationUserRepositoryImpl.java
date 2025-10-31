package org.example.chatflow.repository.impl;

import java.util.Collections;
import java.util.List;
import org.example.chatflow.mapper.ConversationUserMapper;
import org.example.chatflow.model.entity.ConversationUser;
import org.example.chatflow.repository.ConversationUserRepository;
import org.springframework.stereotype.Repository;

/**
 * Conversation-user relation repository implementation.
 */
@Repository
public class ConversationUserRepositoryImpl
        extends BaseRepositoryImpl<ConversationUserMapper, ConversationUser, Long>
        implements ConversationUserRepository {

    @Override
    public List<ConversationUser> findByMemberId(Long memberId) {
        if (memberId == null) {
            return Collections.emptyList();
        }
        return lambdaQuery()
            .eq(ConversationUser::getMemberId, memberId)
            .list();
    }
}
