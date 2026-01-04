package org.example.chatflow.service.impl;

import com.aliyuncs.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.common.constants.FileSourceTypeConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.*;
import org.example.chatflow.common.exception.BusinessException;
import org.example.chatflow.model.dto.friend.AddRequestDTO;
import org.example.chatflow.model.dto.friend.AgreeRequestDTO;
import org.example.chatflow.model.entity.FriendRelation;
import org.example.chatflow.model.entity.FriendRequest;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.model.entity.ConversationUser;
import org.example.chatflow.model.vo.FriendDetailVO;
import org.example.chatflow.model.vo.FriendRequestListTotalVO;
import org.example.chatflow.model.vo.FriendRequestListVO;
import org.example.chatflow.model.vo.GetFriendListVO;
import org.example.chatflow.repository.ConversationUserRepository;
import org.example.chatflow.repository.FriendRelationRepository;
import org.example.chatflow.repository.FriendRequestRepository;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.service.ConversationService;
import org.example.chatflow.service.FileService;
import org.example.chatflow.service.FriendService;
import org.example.chatflow.service.OnlineUserService;
import org.example.chatflow.support.CurrentUserAccessor;
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

    private final ConversationService conversationService;

    private final ConversationUserRepository conversationUserRepository;

    private final CurrentUserAccessor currentUserAccessor;

    private final OnlineUserService onlineUserService;

    private final FileService fileService;

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
            getFriendListVO.setId(user.getId());
//            getFriendListVO.setAvatarFullUrl(fileService.getLatestFullUrl(
//                    FileSourceTypeConstant.USER_AVATAR,
//                    user.getId(),
//                    user.getAvatarUrl()
//            ));
            getFriendListVO.setRemark(StringUtils.isEmpty(friendRelation.getRemark()) ?
                    friendRelation.getRemark() :
                    user.getNickname());
            boolean online = onlineUserService.isUserOnline(user.getId());
            getFriendListVO.setStatus(online ? OnlineStatus.ONLINE.getCode() : OnlineStatus.OFFLINE.getCode());
            getFriendListVOList.add(getFriendListVO);
        }
        return CurlResponse.success(getFriendListVOList);
    }

    /**
     * 申请添加好友
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CurlResponse<String> addFriendRequest(AddRequestDTO dto) {
        Long userId = ThreadLocalUtil.getUserId();
        User friend = checkFriendIsExists(dto.getReceiverId());

        if (userId.equals(friend.getId())) {
            throw new BusinessException(ErrorCode.REQUESTID_EQUALS_RECEIVERID);
        }
        checkFriendRequestIsExists(userId,friend.getId());

        FriendRequest friendRequest = bulidRequest(userId,friend,dto);

        //双方已存在好友关系并存在单向删除，自动建立好友关系
        FriendRelation userToFriend = friendRelationRepository.findByUserAndFriendId(userId,friend.getId());
        FriendRelation friendToUser = friendRelationRepository.findByUserAndFriendId(friend.getId(),userId);
        if (userToFriend != null && friendToUser != null) {
            if (Deleted.HAS_NOT_DELETED.getCode().equals(friendToUser.getDeleted()) &&
                    Deleted.HAS_NOT_DELETED.getCode().equals(userToFriend.getDeleted()))  {
                throw  new BusinessException(ErrorCode.ALREADY_FRIEND_RELATION);
            }
            else if (Deleted.HAS_NOT_DELETED.getCode().equals(friendToUser.getDeleted()) &&
                    Deleted.HAS_DELETED.getCode().equals(userToFriend.getDeleted())) {
                userToFriend.setDeleted(Deleted.HAS_NOT_DELETED.getCode());
                VerifyUtil.ensureOperationSucceeded(friendRelationRepository.update(userToFriend),ErrorCode.AGREE_FRIEND_FAIL);
                friendRequest.setRequestStatus(RequestStatus.APPROVED.getCode());
                friendRequest.setHandledAt(System.currentTimeMillis()/1000);
                conversationService.ensurePrivateConversation(userId, friend.getId());
                return CurlResponse.success("好友添加成功");
            }
        }
        VerifyUtil.ensureOperationSucceeded(
                friendRequestRepository.save(friendRequest),
                ErrorCode.FRIEND_REQUEST_ADD_FAIL
        );
        return CurlResponse.success("好友申请成功");
    }


    /**
     * 删除好友
     */
    @Override
    public CurlResponse<String> deleteFriend(Long param) {
        User user = checkUserIsExists();
        User friend = checkFriendIsExists(param);
        //查询好友关系
        FriendRelation userToFriend = friendRelationRepository
                .findByUserAndFriendId(user.getId(), friend.getId());
        VerifyUtil.isTrue(userToFriend == null,ErrorCode.FRIEND_RELATION_NOT_EXISTS);
        userToFriend.setDeleted(Deleted.HAS_DELETED.getCode());
        //删除好友关系
        VerifyUtil.ensureOperationSucceeded(friendRelationRepository.update(userToFriend),ErrorCode.DELETE_FRIEND_FAIL);
        hideConversationForUser(user.getId(), friend.getId());
        return CurlResponse.success("删除成功");
    }

    /**
     * 申请添加好友列表
     */
    @Override
    public CurlResponse<FriendRequestListTotalVO> friendRequestList() {
        Long userId = ThreadLocalUtil.getUserId();
        List<FriendRequest> outGoIn = friendRequestRepository.findByRequesterId(userId);
        List<FriendRequest> inComIn = friendRequestRepository.findByReceiverId(userId);

        // 收集涉及的用户ID
        Set<Long> userIds = outGoIn.stream().map(FriendRequest::getReceiverId).collect(Collectors.toSet());
        userIds.addAll(inComIn.stream().map(FriendRequest::getRequesterId).collect(Collectors.toSet()));
        Map<Long, User> friendMap = userRepository.getUsersMapByIds(userIds);

        List<FriendRequestListVO> voList = new ArrayList<>(outGoIn.size() + inComIn.size());

        int pendingCount = 0;
        // 我发出的申请
        for (FriendRequest friendRequest : outGoIn) {
            User receiver = friendMap.get(friendRequest.getReceiverId());
            FriendRequestListVO vo = buildReqiestListVO(receiver, friendRequest, Direction.USER_TO_FRIEND);
            if (vo != null) {
                if (vo.getRequestStatus().equals(RequestStatus.PENDING.getCode()))
                    pendingCount++;
                voList.add(vo);
            }
        }



        // 我收到的申请
        for (FriendRequest friendRequest : inComIn) {
            User requester = friendMap.get(friendRequest.getRequesterId());
            FriendRequestListVO vo = buildReqiestListVO(requester, friendRequest, Direction.FRIEND_TO_USER);
            if (vo != null) {
                if (vo.getRequestStatus().equals(RequestStatus.PENDING.getCode()))
                    pendingCount++;
                voList.add(vo);
            }
        }
        FriendRequestListTotalVO vo = new FriendRequestListTotalVO();
        vo.setTotal(inComIn.size() +  outGoIn.size());
        vo.setFriendRequestList(voList);
        vo.setPendingCount(pendingCount);
        return CurlResponse.success(vo);
    }

    private FriendRequestListVO buildReqiestListVO(User user, FriendRequest friendRequest, Direction direction) {
        if (user == null || friendRequest == null) {
            return null;
        }
        FriendRequestListVO vo = new FriendRequestListVO();
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
//        vo.setAvatarFullUrl(fileService.getLatestFullUrl(
//                FileSourceTypeConstant.USER_AVATAR,
//                user.getId(),
//                user.getAvatarUrl()
//        ));
        vo.setApplyMessage(friendRequest.getApplyMessage());
        vo.setCreateTime(friendRequest.getCreateTime());
        vo.setApplyDirection(direction.getCode());
        vo.setRequestStatus(friendRequest.getRequestStatus());
        return vo;
    }


    /**
     * 同意好友申请
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CurlResponse<String> agreeFriendRequest(AgreeRequestDTO dto) {
        Long userId = ThreadLocalUtil.getUserId();
        checkFriendIsExists(dto.getFriendId());
        checkFriendRequestNotExists(userId,dto.getFriendId());
        FriendRequest request = checkFriendRequestNotExists(userId,dto.getFriendId());

        FriendRelation userToFriend = friendRelationRepository.findByUserAndFriendId(userId,dto.getFriendId());
        FriendRelation friendToUser = friendRelationRepository.findByUserAndFriendId(dto.getFriendId(),userId);

        FriendRelation newUserToFriend = new FriendRelation();
        newUserToFriend.setUserId(userId);
        newUserToFriend.setFriendId(dto.getFriendId());
        newUserToFriend.setRemark(dto.getRemark());
        newUserToFriend.setDeleted(Deleted.HAS_NOT_DELETED.getCode());

        FriendRelation newFriendToUser = new FriendRelation();
        newFriendToUser.setFriendId(userId);
        newFriendToUser.setUserId(dto.getFriendId());
        newFriendToUser.setRemark(request.getApplyRemark());
        newFriendToUser.setDeleted(Deleted.HAS_NOT_DELETED.getCode());
        List<FriendRelation> relations = new  ArrayList<>();

        if (userToFriend == null) {
            relations.add(newUserToFriend);
        }
        if (friendToUser == null) {
            relations.add(newFriendToUser);
        }
        //添加好友关系
        VerifyUtil.ensureOperationSucceeded(friendRelationRepository.saveBatch(relations),ErrorCode.AGREE_FRIEND_FAIL);
        //修改申请状态、时间
        request.setRequestStatus(RequestStatus.APPROVED.getCode());
        request.setHandledAt(System.currentTimeMillis()/1000);
        VerifyUtil.ensureOperationSucceeded(friendRequestRepository.update(request),ErrorCode.AGREE_FRIEND_FAIL);
        conversationService.ensurePrivateConversation(userId, dto.getFriendId());
        return CurlResponse.success("成功添加好友");
    }

    /**
     * 拒绝好友申请
     */
    @Override
    public CurlResponse<String> disagreeFriendRequest(Long param) {
        Long userId = ThreadLocalUtil.getUserId();
        FriendRequest request = checkFriendRequestNotExists(userId,param);
        request.setRequestStatus(RequestStatus.REJECTED.getCode());
        request.setHandledAt(System.currentTimeMillis()/1000);
        VerifyUtil.ensureOperationSucceeded(friendRequestRepository.update(request),ErrorCode.AGREE_FRIEND_FAIL);
        return CurlResponse.success("成功拒绝好友申请");
    }

    /**
     * 好友详情
     */
    @Override
    public CurlResponse<FriendDetailVO> friendDetail(Long param) {
        User user = currentUserAccessor.getCurrentUser();
        User friend  = userRepository.findById(param).
                orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTS));
        FriendDetailVO friendDetailVO = FriendDetailVO.FriendDetailVOMapper.INSTANCE.toVO(friend);
//        friendDetailVO.setAvatarFullUrl(fileService.getLatestFullUrl(
//                FileSourceTypeConstant.USER_AVATAR,
//                friend.getId(),
//                friend.getAvatarUrl()
//        ));
        FriendRelation relation = friendRelationRepository.findByUserAndFriendId(user.getId(),friend.getId());
        VerifyUtil.isTrue(relation == null,ErrorCode.FRIEND_RELATION_NOT_EXISTS);
        friendDetailVO.setRemark(relation.getRemark());
        boolean online = onlineUserService.isUserOnline(friend.getId());
        friendDetailVO.setStatus(online ? OnlineStatus.ONLINE.getCode() : OnlineStatus.OFFLINE.getCode());
        return CurlResponse.success(friendDetailVO);
    }

    private void hideConversationForUser(Long userId, Long friendId) {
        Long conversationId = conversationService.findExistingPrivateConversation(userId, friendId);
        if (conversationId == null) {
            return;
        }
        ConversationUser relation = conversationUserRepository.findByConversationIdAndMemberId(conversationId, userId);
        if (relation == null) {
            return;
        }
        if (!Objects.equals(relation.getStatus(), ConversationStatus.HIDDEN.getCode())) {
            relation.setStatus(ConversationStatus.HIDDEN.getCode());
            VerifyUtil.ensureOperationSucceeded(conversationUserRepository.update(relation),
                    ErrorCode.CONVERSATION_USER_UPDATE_FAIL);
        }
    }

    private FriendRequest checkFriendRequestNotExists(Long userId, Long friendId) {
        FriendRequest friendRequest = friendRequestRepository.findByRequesterAndReceiverId(friendId,
                userId);
        VerifyUtil.isTrue(friendRequest == null,ErrorCode.FRIEND_REQUEST_NOT_EXISTS);
        return friendRequest;
    }

    private FriendRequest checkFriendRequestIsExists(Long userId, Long friendId) {
        FriendRequest friendRequest = friendRequestRepository.findByRequesterAndReceiverId(friendId,
                userId);
        VerifyUtil.isTrue(friendRequest != null &&
                        !friendRequest.getRequestStatus().equals(RequestStatus.REJECTED.getCode()),
                ErrorCode.FRIEND_REQUEST_EXISTS);
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
        friendRequest.setApplyRemark(!StringUtils.isEmpty(remark) ? remark : friend.getNickname());
        friendRequest.setRequestStatus(RequestStatus.PENDING.getCode());
        return friendRequest;
    }

    private User checkUserIsExists(){
        return currentUserAccessor.getCurrentUser();
    }
}
