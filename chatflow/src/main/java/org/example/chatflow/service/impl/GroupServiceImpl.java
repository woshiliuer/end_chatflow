package org.example.chatflow.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.common.constants.OssConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ConversationStatus;
import org.example.chatflow.common.enums.ConversationType;
import org.example.chatflow.common.enums.Deleted;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.common.enums.GroupRole;
import org.example.chatflow.model.dto.group.AddGroupDTO;
import org.example.chatflow.model.entity.ChatGroup;
import org.example.chatflow.model.entity.Conversation;
import org.example.chatflow.model.entity.ConversationUser;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.model.vo.GroupDetailVO;
import org.example.chatflow.model.vo.GroupListTotalVO;
import org.example.chatflow.model.vo.GroupListVO;
import org.example.chatflow.model.vo.GroupMemberVO;
import org.example.chatflow.repository.*;
import org.example.chatflow.service.GroupService;
import org.example.chatflow.support.CurrentUserAccessor;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author by zzr
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GroupServiceImpl implements GroupService {

    private final ChatGroupRepository  chatGroupRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationUserRepository conversationUserRepository;
    private final UserRepository userRepository;
    private final FriendRelationRepository friendRelationRepository;
    private final CurrentUserAccessor currentUserAccessor;
    /**
     * 新建群聊
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CurlResponse<String> addGroup(AddGroupDTO dto) {
        User user = checkUserIsExists();
        //先新建一个群聊
        ChatGroup chatGroup = AddGroupDTO.AddGroupDTOMapper.INSTANCE.toChatGroup(dto);
        chatGroup.setOwnerId(user.getId());
        //群公告默认为空
        chatGroup.setAnnouncement("");
        //默认头像
        chatGroup.setGroupAvatarUrl(OssConstant.DEFAULT_GROUP_AVATAR);
        chatGroup.setDeleted(Deleted.HAS_NOT_DELETED.getCode());
        //保存群聊
        VerifyUtil.ensureOperationSucceeded(chatGroupRepository.save(chatGroup), ErrorCode.GROUP_SAVE_FAIL);

        //创建会话
        Conversation conversation = new  Conversation();
        conversation.setConversationType(ConversationType.GROUP.getCode());
        conversation.setGroupId(chatGroup.getId());
        VerifyUtil.ensureOperationSucceeded(
                conversationRepository.save(conversation),ErrorCode.CONVERSATION_SAVE_FAIL
        );


        //验证是否所有成员都有好友关系

        checkFriendRelation(user.getId(),dto.getMemberIds());

        //添加会话，用户关系
        Long conversationId = conversation.getId();
        List<ConversationUser> conversationUserList = new ArrayList<>();
        conversationUserList.add(buildConversationUser(conversationId, user.getId(),GroupRole.OWNER));
        //创建成员和会话的关系
        for (Long memberId : dto.getMemberIds()) {
            conversationUserList.add(buildConversationUser(conversationId,memberId,GroupRole.MEMBER));
        }
        VerifyUtil.ensureOperationSucceeded(
                conversationUserRepository.saveBatch(conversationUserList),
                ErrorCode.CONVERSATION_USER_SAVE_FAIL
        );
        return CurlResponse.success("群聊创建成功");
    }

    /**
     * 群聊列表
     */
    @Override
    public CurlResponse<List<GroupListTotalVO>> groupList() {
        User user = checkUserIsExists();
        List<ConversationUser> membershipList = conversationUserRepository.findByMemberId(user.getId());
        if (membershipList.isEmpty()) {
            GroupListTotalVO emptyTotal = new GroupListTotalVO();
            emptyTotal.setGroupList(Collections.emptyList());
            emptyTotal.setTotal(0);
            return CurlResponse.success(Collections.singletonList(emptyTotal));
        }

        Map<Long, Integer> roleByConversation = membershipList.stream()
            .filter(relation -> relation.getConversationId() != null)
            .collect(Collectors.toMap(ConversationUser::getConversationId, ConversationUser::getRole, (left, right) -> left));

        Set<Long> conversationIds = membershipList.stream()
            .map(ConversationUser::getConversationId)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        if (conversationIds.isEmpty()) {
            GroupListTotalVO emptyTotal = new GroupListTotalVO();
            emptyTotal.setGroupList(Collections.emptyList());
            emptyTotal.setTotal(0);
            return CurlResponse.success(Collections.singletonList(emptyTotal));
        }

        List<Conversation> conversations = conversationRepository.findByIds(conversationIds);
        List<Conversation> groupConversations = conversations.stream()
            .filter(conversation -> Objects.equals(conversation.getConversationType(), ConversationType.GROUP.getCode()))
            .filter(conversation -> conversation.getGroupId() != null)
            .collect(Collectors.toList());

        if (groupConversations.isEmpty()) {
            GroupListTotalVO emptyTotal = new GroupListTotalVO();
            emptyTotal.setGroupList(Collections.emptyList());
            emptyTotal.setTotal(0);
            return CurlResponse.success(Collections.singletonList(emptyTotal));
        }

        Set<Long> groupIds = groupConversations.stream()
            .map(Conversation::getGroupId)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        List<ChatGroup> chatGroups = chatGroupRepository.findNormalByIds(groupIds);
        Map<Long, ChatGroup> groupById = chatGroups.stream()
            .collect(Collectors.toMap(ChatGroup::getId, Function.identity()));

        List<GroupListVO> groupList = groupConversations.stream()
            .map(conversation -> buildGroupListVO(conversation, groupById, roleByConversation))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        GroupListTotalVO totalVO = new GroupListTotalVO();
        totalVO.setGroupList(groupList);
        totalVO.setTotal(groupList.size());

        return CurlResponse.success(Collections.singletonList(totalVO));
    }

    @Override
    public CurlResponse<GroupDetailVO> groupDetail(Long groupId) {
        ChatGroup group = chatGroupRepository.findNormalById(groupId);
        VerifyUtil.isTrue(group == null,ErrorCode.GROUP_NOT_EXISTS);
        GroupDetailVO vo = GroupDetailVO.GroupDetailVOMapper.INSTANCE.toVO(group);
        //根据群聊id查询群聊会话
        Conversation conversation = conversationRepository.findByGroupId(groupId);
        VerifyUtil.isTrue(conversation == null,ErrorCode.CONVERSATION_NOT_FOUND);
        //根据会话id查询所有的成员
        List<ConversationUser> conversationUserList = conversationUserRepository
                .findByConversationIds(Collections.singleton(conversation.getId()));
        
        // 按照memberId分组，建立memberId到角色的映射关系
        Map<Long, Integer> roleByMemberId = conversationUserList.stream()
                .collect(Collectors.toMap(ConversationUser::getMemberId, ConversationUser::getRole, (left, right) -> left));

        Set<Long> memberIdList = conversationUserList.stream()
                .map(ConversationUser::getMemberId).collect(Collectors.toSet());
        List<User> memberList = userRepository.findExistByIds(memberIdList);
        List<GroupMemberVO> groupMemberVOList = new ArrayList<>();
        for (User member : memberList) {
            GroupMemberVO groupMemberVO = new GroupMemberVO();
            groupMemberVO.setMemberId(member.getId());
            groupMemberVO.setNickname(member.getNickname());
            // 从映射中获取该成员的角色，默认为普通成员
            groupMemberVO.setRole(roleByMemberId.get(member.getId()));
            groupMemberVO.setAvatarFullUrl(OssConstant.buildFullUrl(member.getAvatarUrl()));
            groupMemberVOList.add(groupMemberVO);
        }

        vo.setMembers(groupMemberVOList);

        return CurlResponse.success(vo);
    }

    private ConversationUser buildConversationUser(Long conversationId, Long memberId, GroupRole role) {
        ConversationUser conversationUser = new ConversationUser();
        conversationUser.setConversationId(conversationId);
        conversationUser.setMemberId(memberId);
        conversationUser.setRole(role.getCode());
        conversationUser.setJoinTime(System.currentTimeMillis()/1000);
        conversationUser.setStatus(ConversationStatus.NORMAL.getCode());
        return conversationUser;
    }

    private void checkFriendRelation(Long userId, List<Long> memberIds) {
        int count = friendRelationRepository.countFriendRelationByUserIdAndFriends(userId,memberIds);
        VerifyUtil.isFalse(count == memberIds.size(),ErrorCode.FRIEND_RELATION_NOT_EXISTS);
    }

    private User checkUserIsExists(){
        return currentUserAccessor.getCurrentUser();
    }

    private GroupListVO buildGroupListVO(Conversation conversation,
                                         Map<Long, ChatGroup> groupById,
                                         Map<Long, Integer> roleByConversation) {
        ChatGroup chatGroup = groupById.get(conversation.getGroupId());
        if (chatGroup == null) {
            return null;
        }
        GroupListVO vo = GroupListVO.GroupListVOMapper.INSTANCE.toVO(chatGroup);
        vo.setGroupAvatarFullUrl(OssConstant.buildFullUrl(chatGroup.getGroupAvatarUrl()));
        vo.setRole(roleByConversation.getOrDefault(conversation.getId(), GroupRole.MEMBER.getCode()));
        return vo;
    }
}
