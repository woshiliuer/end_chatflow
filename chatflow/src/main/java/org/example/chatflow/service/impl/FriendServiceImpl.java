package org.example.chatflow.service.impl;

import com.aliyuncs.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.common.constants.OssConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.OnlineStatus;
import org.example.chatflow.model.entity.FriendRelation;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.model.vo.GetFriendListVO;
import org.example.chatflow.repository.FriendRelationRepository;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.service.FriendService;
import org.example.chatflow.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by zzr
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FriendServiceImpl implements FriendService {

    private final FriendRelationRepository friendRelationRepository;

    private final UserRepository userRepository;
    /**
     * 获取好友列表
     */
    @Override
    public CurlResponse<List<GetFriendListVO>> getFriends() {
        Long userId = ThreadLocalUtil.getUserId();
        List<FriendRelation> ownFriends = friendRelationRepository.getFriendRelationByUserId(userId);
        //获取好友的用户信息
        Set<Long> allFriendIds = new HashSet<>();
        allFriendIds.addAll(ownFriends.stream().map(
                FriendRelation::getFriendId
        ).collect(Collectors.toSet()));
        Map<Long, User> friendMap = userRepository.getUsersMapByIds(allFriendIds);

        List<GetFriendListVO> getFriendListVOList = new ArrayList<>();

        for (FriendRelation friendRelation : ownFriends) {
            GetFriendListVO getFriendListVO = new  GetFriendListVO();
            User user = friendMap.get(friendRelation.getFriendId());
            getFriendListVO.setAvatarFullUrl(OssConstant.buildFullUrl(user.getAvatarUrl()));
            getFriendListVO.setRemark(StringUtils.isEmpty(friendRelation.getRemark()) ?
                    friendRelation.getRemark() :
                    user.getNickname());
            //TODO 在线状态填充,先默认都在线
            getFriendListVO.setStatus(OnlineStatus.ONLINE.getCode());
            getFriendListVOList.add(getFriendListVO);
        }
        return CurlResponse.success(getFriendListVOList);
    }

    /**
     * 申请添加好友
     */
    @Override
    public CurlResponse<String> addFriendRequest(Long param) {

        return null;
    }
}
