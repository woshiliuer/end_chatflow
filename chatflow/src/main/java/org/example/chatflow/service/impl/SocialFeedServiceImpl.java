package org.example.chatflow.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.chatflow.common.constants.OssConstant;
import org.example.chatflow.common.constants.FileSourceTypeConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.common.enums.SocialFeedLikeStatus;
import org.example.chatflow.model.dto.common.FileCommonDTO;
import org.example.chatflow.model.dto.social.SocialFeedListQueryDTO;
import org.example.chatflow.model.dto.social.SocialFeedPublishDTO;
import org.example.chatflow.model.entity.SocialFeedComment;
import org.example.chatflow.model.entity.SocialFeedLike;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.repository.SocialFeedRepository;
import org.example.chatflow.model.entity.SocialFeed;
import org.example.chatflow.repository.SocialFeedCommentRepository;
import org.example.chatflow.repository.SocialFeedLikeRepository;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.model.vo.common.FileCommonVO;
import org.example.chatflow.model.vo.social.SocialFeedCommentItemVO;
import org.example.chatflow.model.vo.social.SocialFeedDetailVO;
import org.example.chatflow.model.vo.social.SocialFeedListTotalVO;
import org.example.chatflow.model.vo.social.SocialFeedListVO;
import org.example.chatflow.service.FileService;
import org.example.chatflow.service.SocialFeedService;
import org.example.chatflow.support.CurrentUserAccessor;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SocialFeedServiceImpl implements SocialFeedService {

    private final SocialFeedRepository socialFeedRepository;
    private final FileService fileService;
    private final SocialFeedLikeRepository socialFeedLikeRepository;
    private final SocialFeedCommentRepository socialFeedCommentRepository;
    private final UserRepository userRepository;
    private final CurrentUserAccessor currentUserAccessor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<Long> publish(SocialFeedPublishDTO dto) {
        VerifyUtil.isTrue(dto == null, ErrorCode.VALIDATION_ERROR);

        String content = StringUtils.trimToNull(dto.getContent());
        List<FileCommonDTO> files = dto.getFiles();
        boolean hasFiles = files != null && !files.isEmpty();
        VerifyUtil.isTrue(content == null && !hasFiles, ErrorCode.VALIDATION_ERROR);

        SocialFeed feed = new SocialFeed();
        feed.setContent(content);

        VerifyUtil.ensureOperationSucceeded(socialFeedRepository.save(feed), ErrorCode.INTERNAL_ERROR);
        Long feedId = feed.getId();
        VerifyUtil.isTrue(feedId == null, ErrorCode.INTERNAL_ERROR);

        if (hasFiles) {
            List<FileCommonDTO> bindList = new ArrayList<>();
            for (FileCommonDTO f : files) {
                if (f == null) {
                    continue;
                }
                VerifyUtil.isTrue(StringUtils.isBlank(f.getFilePath()), ErrorCode.FILE_IS_NULL);

                FileCommonDTO bind = FileCommonDTO.FileCommonDTOMapper.INSTANCE.toDTO(
                        FileSourceTypeConstant.SOCIAL_FEED_FILE,
                        feedId,
                        f.getFileType(),
                        f.getFileName(),
                        f.getFileSize(),
                        f.getFilePath(),
                        f.getFileDesc()
                );
                bindList.add(bind);
            }
            if (!bindList.isEmpty()) {
                VerifyUtil.ensureOperationSucceeded(fileService.saveBatchFile(bindList), ErrorCode.INTERNAL_ERROR);
            }
        }

        return CurlResponse.success(feedId);
    }

    @Override
    public CurlResponse<SocialFeedListTotalVO> list(SocialFeedListQueryDTO dto) {
        VerifyUtil.isTrue(dto == null, ErrorCode.VALIDATION_ERROR);
        VerifyUtil.isTrue(dto.getPage() == null || dto.getSize() == null, ErrorCode.VALIDATION_ERROR);

        Page<SocialFeed> page = socialFeedRepository.pageByContent(dto.getPage(), dto.getSize(), dto.getContent());
        List<SocialFeed> feeds = page == null ? Collections.emptyList() : page.getRecords();

        Set<Long> feedIds = new HashSet<>();
        Set<Long> authorIds = new HashSet<>();
        if (feeds != null) {
            for (SocialFeed feed : feeds) {
                if (feed == null) {
                    continue;
                }
                if (feed.getId() != null) {
                    feedIds.add(feed.getId());
                }
                if (feed.getCreateUserId() != null) {
                    authorIds.add(feed.getCreateUserId());
                }
            }
        }

        Map<Long, Long> likeCountMap = socialFeedLikeRepository.countValidByFeedIds(feedIds);
        Map<Long, Long> commentCountMap = socialFeedCommentRepository.countByFeedIds(feedIds);
        Map<Long, User> userMap = userRepository.findUserMapByIds(authorIds);
        Map<Long, String> avatarMap = fileService.getLatestFullUrlMap(
            FileSourceTypeConstant.USER_AVATAR,
            authorIds,
            OssConstant.DEFAULT_AVATAR
        );

        List<SocialFeedListVO> resultList = new ArrayList<>();
        if (feeds != null) {
            for (SocialFeed feed : feeds) {
                if (feed == null || feed.getId() == null) {
                    continue;
                }
                SocialFeedListVO vo = SocialFeedListVO.SocialFeedListVOMapper.INSTANCE.toVO(feed);
                vo.setId(feed.getId());

                Long authorId = feed.getCreateUserId();
                if (authorId != null) {
                    User author = userMap.get(authorId);
                    vo.setNickname(author == null ? null : author.getNickname());
                    vo.setAvatarFullUrl(avatarMap.get(authorId));
                }

                vo.setLikeCount(Optional.ofNullable(likeCountMap.get(feed.getId())).orElse(0L));
                vo.setCommentCount(Optional.ofNullable(commentCountMap.get(feed.getId())).orElse(0L));
                resultList.add(vo);
            }
        }

        SocialFeedListTotalVO totalVO = new SocialFeedListTotalVO();
        totalVO.setFeedList(resultList);
        totalVO.setTotal(page == null ? 0L : page.getTotal());
        return CurlResponse.success(totalVO);
    }

    @Override
    public CurlResponse<SocialFeedDetailVO> detail(Long feedId) {
        VerifyUtil.isTrue(feedId == null, ErrorCode.VALIDATION_ERROR);

        SocialFeed feed = socialFeedRepository.findById(feedId).orElse(null);
        VerifyUtil.isTrue(feed == null, ErrorCode.BUSINESS_ERROR);

        Long authorId = feed.getCreateUserId();
        Set<Long> authorIds = authorId == null ? Collections.emptySet() : Collections.singleton(authorId);
        Map<Long, User> userMap = authorIds.isEmpty() ? Collections.emptyMap() : userRepository.findUserMapByIds(authorIds);
        Map<Long, String> avatarMap = authorIds.isEmpty() ? Collections.emptyMap() : fileService.getLatestFullUrlMap(
            FileSourceTypeConstant.USER_AVATAR,
            authorIds,
            OssConstant.DEFAULT_AVATAR
        );

        Set<Long> singleFeedIdSet = Collections.singleton(feedId);
        Map<Long, Long> likeCountMap = socialFeedLikeRepository.countValidByFeedIds(singleFeedIdSet);
        Map<Long, Long> commentCountMap = socialFeedCommentRepository.countByFeedIds(singleFeedIdSet);

        Long currentUserId = currentUserAccessor.getCurrentUser().getId();
        VerifyUtil.isTrue(currentUserId == null, ErrorCode.USER_NOT_LOGIN);

        SocialFeedLike myLike = socialFeedLikeRepository.findByFeedIdAndUserId(feedId, currentUserId).orElse(null);
        boolean liked = myLike != null && SocialFeedLikeStatus.VALID.getCode().equals(myLike.getStatus());

        List<FileCommonVO> files = fileService.listBySource(FileSourceTypeConstant.SOCIAL_FEED_FILE, feedId);
        List<SocialFeedComment> comments = socialFeedCommentRepository.findByFeedId(feedId);

        Set<Long> commenterIds = comments == null ? Collections.emptySet() : comments.stream()
            .filter(c -> c != null && c.getUserId() != null)
            .map(SocialFeedComment::getUserId)
            .collect(Collectors.toSet());
        Map<Long, User> commenterMap = userRepository.findUserMapByIds(commenterIds);
        Map<Long, String> commenterAvatarMap = fileService.getLatestFullUrlMap(
            FileSourceTypeConstant.USER_AVATAR,
            commenterIds,
            OssConstant.DEFAULT_AVATAR
        );

        List<SocialFeedCommentItemVO> commentVos = new ArrayList<>();
        if (comments != null) {
            for (SocialFeedComment c : comments) {
                if (c == null) {
                    continue;
                }
                SocialFeedCommentItemVO cvo = SocialFeedCommentItemVO.SocialFeedCommentItemVOMapper.INSTANCE.toVO(c);
                Long uid = c.getUserId();
                if (uid != null) {
                    User u = commenterMap.get(uid);
                    cvo.setNickname(u == null ? null : u.getNickname());
                    cvo.setAvatarFullUrl(commenterAvatarMap.get(uid));
                }
                commentVos.add(cvo);
            }
        }

        SocialFeedDetailVO vo = SocialFeedDetailVO.SocialFeedDetailVOMapper.INSTANCE.toVO(feed);
        vo.setId(feed.getId());
        if (authorId != null) {
            User author = userMap.get(authorId);
            vo.setNickname(author == null ? null : author.getNickname());
            vo.setAvatarFullUrl(avatarMap.get(authorId));
        }
        vo.setLikeCount(Optional.ofNullable(likeCountMap.get(feedId)).orElse(0L));
        vo.setCommentCount(Optional.ofNullable(commentCountMap.get(feedId)).orElse(0L));
        vo.setIsLike(liked);
        vo.setFiles(files);
        vo.setComments(commentVos);
        return CurlResponse.success(vo);
    }
}
