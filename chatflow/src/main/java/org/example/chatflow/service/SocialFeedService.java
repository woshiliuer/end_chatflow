package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.social.SocialFeedPublishDTO;
import org.example.chatflow.model.dto.social.SocialFeedListQueryDTO;
import org.example.chatflow.model.vo.social.SocialFeedDetailVO;
import org.example.chatflow.model.vo.social.SocialFeedListTotalVO;

/**
 * 社交动态服务
 */
public interface SocialFeedService {

    CurlResponse<Long> publish(SocialFeedPublishDTO dto);

    CurlResponse<SocialFeedListTotalVO> list(SocialFeedListQueryDTO dto);

    CurlResponse<SocialFeedDetailVO> detail(Long feedId);

    CurlResponse<Boolean> delete(Long feedId);
}
