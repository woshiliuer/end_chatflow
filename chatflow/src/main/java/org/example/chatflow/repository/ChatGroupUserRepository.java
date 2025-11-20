package org.example.chatflow.repository;

import org.example.chatflow.model.entity.ChatGroupUser;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ChatGroupUserRepository extends BaseRepository<ChatGroupUser, Long> {

    List<ChatGroupUser> findByGroupId(Long groupId);

    List<ChatGroupUser> findByGroupIds(Set<Long> groupIds);

    List<ChatGroupUser> findByMemberId(Long memberId);
}
