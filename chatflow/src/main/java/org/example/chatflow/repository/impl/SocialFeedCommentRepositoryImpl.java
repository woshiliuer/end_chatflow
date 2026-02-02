package org.example.chatflow.repository.impl;

import org.example.chatflow.mapper.SocialFeedCommentMapper;
import org.example.chatflow.model.entity.SocialFeedComment;
import org.example.chatflow.repository.SocialFeedCommentRepository;
import org.springframework.stereotype.Repository;

@Repository
public class SocialFeedCommentRepositoryImpl
    extends BaseRepositoryImpl<SocialFeedCommentMapper, SocialFeedComment, Long>
    implements SocialFeedCommentRepository {
}
