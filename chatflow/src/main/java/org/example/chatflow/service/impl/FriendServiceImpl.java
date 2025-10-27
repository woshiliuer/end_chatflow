package org.example.chatflow.service.impl;

import com.aliyuncs.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.common.constants.OssConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.common.enums.OnlineStatus;
import org.example.chatflow.common.enums.RequestStatus;
import org.example.chatflow.model.dto.friend.AddRequestDTO;
import org.example.chatflow.model.dto.friend.AgreeRequestDTO;
import org.example.chatflow.model.entity.FriendRelation;
import org.example.chatflow.model.entity.FriendRequest;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.model.vo.GetFriendListVO;
import org.example.chatflow.repository.FriendRelationRepository;
import org.example.chatflow.repository.FriendRequestRepository;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.service.FriendService;
import org.example.chatflow.utils.ThreadLocalUtil;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final FriendRequestRepository friendRequestRepository;
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
    public CurlResponse<String> addFriendRequest(AddRequestDTO dto) {
        Long userId = ThreadLocalUtil.getUserId();
        User friend = checkFriendIsExists(dto.getReceiverId());

        FriendRequest friendRequest = bulidRequest(userId,friend,dto);

        VerifyUtil.ensureOperationSucceeded(
                friendRequestRepository.save(friendRequest),
                ErrorCode.FRIEND_REQUEST_ADD_FAIL
        );
        return CurlResponse.success("好友申请成功");
    }


    /**
     * 同意好友申请
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CurlResponse<String> agreeFriendRequest(AgreeRequestDTO dto) {
        Long userId = ThreadLocalUtil.getUserId();
        checkFriendIsExists(dto.getFriendId());
        checkFriendRequestExists(userId,dto.getFriendId());
        FriendRequest request = checkFriendRequestExists(userId,dto.getFriendId());

        FriendRelation friendRelation = new FriendRelation();
        friendRelation.setUserId(userId);
        friendRelation.setFriendId(dto.getFriendId());
        friendRelation.setRemark(dto.getRemark());

        FriendRelation friendRelation1 = new FriendRelation();
        friendRelation1.setFriendId(userId);
        friendRelation1.setUserId(dto.getFriendId());
        friendRelation1.setRemark(request.getApplyRemark());
        List<FriendRelation> relations = new  ArrayList<>();
        relations.add(friendRelation);
        relations.add(friendRelation1);
        //添加好友关系
        VerifyUtil.ensureOperationSucceeded(friendRelationRepository.saveBatch(relations),ErrorCode.AGREE_FRIEND_FAIL);
        //修改申请状态、时间
        request.setRequestStatus(RequestStatus.APPROVED.getCode());
        request.setHandledAt(System.currentTimeMillis()/1000);
        VerifyUtil.ensureOperationSucceeded(friendRequestRepository.update(request),ErrorCode.AGREE_FRIEND_FAIL);
        return CurlResponse.success("成功添加好友");
    }

    /**
     * 拒绝好友申请
     */
    @Override
    public CurlResponse<String> disagreeFriendRequest(Long param) {
        Long userId = ThreadLocalUtil.getUserId();
        FriendRequest request = checkFriendRequestExists(userId,param);
        request.setRequestStatus(RequestStatus.REJECTED.getCode());
        request.setHandledAt(System.currentTimeMillis()/1000);
        VerifyUtil.ensureOperationSucceeded(friendRequestRepository.update(request),ErrorCode.AGREE_FRIEND_FAIL);
        return CurlResponse.success("成功拒绝好友申请");
    }

    private FriendRequest checkFriendRequestExists(Long userId, Long friendId) {
        FriendRequest friendRequest = friendRequestRepository.findByRequesterAndReceiverId(friendId,
                userId);
        VerifyUtil.isTrue(friendRequest == null,ErrorCode.FRIEND_REQUEST_NOT_EXISTS);
        return friendRequest;
    }

    private User checkFriendIsExists(Long id){
        User user = userRepository.findById(id).get();
        VerifyUtil.isTrue(user == null || user.getId() == null || user.getId() <= 0,
                ErrorCode.USER_NOT_LOGIN);
        return user;
    }



    private FriendRequest bulidRequest(Long userId, User friend,AddRequestDTO dto) {
        FriendRequest friendRequest = AddRequestDTO.AddRequestDTOMapper.INSTANCE.toRequest(dto);
        friendRequest.setRequesterId(userId);
        //备注如果为空就用昵称
        String remark = dto.getRemark();
        friendRequest.setApplyRemark(StringUtils.isEmpty(remark) ? remark : friend.getNickname());
        friendRequest.setRequestStatus(RequestStatus.PENDING.getCode());
        return friendRequest;
    }


}
