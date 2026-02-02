package org.example.chatflow.repository.impl;

import org.example.chatflow.mapper.SocialFeedLikeMapper;
import org.example.chatflow.model.entity.SocialFeedLike;
import org.example.chatflow.repository.SocialFeedLikeRepository;
import org.springframework.stereotype.Repository;

@Repository
public class SocialFeedLikeRepositoryImpl
    extends BaseRepositoryImpl<SocialFeedLikeMapper, SocialFeedLike, Long>
    implements SocialFeedLikeRepository {
}
