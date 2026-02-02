package org.example.chatflow.repository.impl;

import org.example.chatflow.mapper.SocialFeedMapper;
import org.example.chatflow.model.entity.SocialFeed;
import org.example.chatflow.repository.SocialFeedRepository;
import org.springframework.stereotype.Repository;

@Repository
public class SocialFeedRepositoryImpl
    extends BaseRepositoryImpl<SocialFeedMapper, SocialFeed, Long>
    implements SocialFeedRepository {
}
