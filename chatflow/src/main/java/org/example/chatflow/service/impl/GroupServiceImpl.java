package org.example.chatflow.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.common.constants.FileSourceTypeConstant;
import org.example.chatflow.common.constants.OssConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.*;
import org.example.chatflow.model.dto.group.AddGroupDTO;
import org.example.chatflow.model.dto.group.EditGroupDTO;
import org.example.chatflow.model.dto.group.InviteGroupMemberDTO;
import org.example.chatflow.model.dto.group.RemoveGroupMemberDTO;
import org.example.chatflow.model.entity.*;
import org.example.chatflow.model.vo.GroupDetailVO;
import org.example.chatflow.model.vo.GroupListTotalVO;
import org.example.chatflow.model.vo.GroupListVO;
import org.example.chatflow.model.vo.GroupMemberVO;
import org.example.chatflow.repository.*;
import org.example.chatflow.service.FileService;
import org.example.chatflow.service.GroupService;
import org.example.chatflow.service.OnlineUserService;
import org.example.chatflow.support.CurrentUserAccessor;
import org.example.chatflow.utils.VerifyUtil;
import org.mapstruct.factory.Mappers;
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
    private final ChatGroupUserRepository chatGroupUserRepository;
    private final CurrentUserAccessor currentUserAccessor;
    private final OnlineUserService onlineUserService;
    private final FileService fileService;
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

//        fileService.saveFile(
//                FileSourceTypeConstant.GROUP_AVATAR,
//                chatGroup.getId(),
//                OssConstant.DEFAULT_GROUP_AVATAR,
//                null,
//                null
//        );

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
        conversationUserList.add(buildConversationUser(conversationId, user.getId()));
        Long groupId = chatGroup.getId();
        List<ChatGroupUser> chatGroupUserList = new ArrayList<>();
        chatGroupUserList.add(buildChatGroupUser(groupId,user.getId(),GroupRole.OWNER));
        //创建成员和会话的关系和用户群聊关系
        for (Long memberId : dto.getMemberIds()) {
            conversationUserList.add(buildConversationUser(conversationId,memberId));
            chatGroupUserList.add(buildChatGroupUser(groupId,memberId,GroupRole.MEMBER));
        }
        VerifyUtil.ensureOperationSucceeded(
                conversationUserRepository.saveBatch(conversationUserList),
                ErrorCode.CONVERSATION_USER_SAVE_FAIL
        );

        VerifyUtil.ensureOperationSucceeded(
                chatGroupUserRepository.saveBatch(chatGroupUserList),
                "群聊创建失败"
        );


        return CurlResponse.success("群聊创建成功");
    }

    /**
     * 群聊列表
     */
    @Override
    public CurlResponse<List<GroupListTotalVO>> groupList() {
        User user = checkUserIsExists();
        List<ChatGroupUser> memberships = chatGroupUserRepository.findByMemberId(user.getId());
        List<ChatGroupUser> activeMemberships = memberships.stream()
                .filter(m -> Objects.equals(m.getStatus(), ChatGroupUserStatus.NORMAL.getCode()))
                .collect(Collectors.toList());

        if (activeMemberships.isEmpty()) {
            GroupListTotalVO emptyTotal = new GroupListTotalVO();
            emptyTotal.setGroupList(Collections.emptyList());
            emptyTotal.setTotal(0);
            return CurlResponse.success(Collections.singletonList(emptyTotal));
        }

        Map<Long, Integer> roleByGroup = activeMemberships.stream()
                .filter(m -> m.getGroupId() != null)
                .collect(Collectors.toMap(ChatGroupUser::getGroupId, ChatGroupUser::getRole, (l, r) -> l));

        Set<Long> groupIds = activeMemberships.stream()
                .map(ChatGroupUser::getGroupId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (groupIds.isEmpty()) {
            GroupListTotalVO emptyTotal = new GroupListTotalVO();
            emptyTotal.setGroupList(Collections.emptyList());
            emptyTotal.setTotal(0);
            return CurlResponse.success(Collections.singletonList(emptyTotal));
        }

        List<ChatGroup> chatGroups = chatGroupRepository.findNormalByIds(groupIds);
        if (chatGroups.isEmpty()) {
            GroupListTotalVO emptyTotal = new GroupListTotalVO();
            emptyTotal.setGroupList(Collections.emptyList());
            emptyTotal.setTotal(0);
            return CurlResponse.success(Collections.singletonList(emptyTotal));
        }

        List<GroupListVO> groupList = chatGroups.stream()
                .map(group -> buildGroupListVO(group, roleByGroup))
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
//        vo.setGroupAvatarFullUrl(fileService.getLatestFullUrl(
//                FileSourceTypeConstant.GROUP_AVATAR,
//                group.getId(),
//                group.getGroupAvatarUrl()
//        ));
        List<ChatGroupUser> groupUsers = chatGroupUserRepository.findByGroupId(groupId).stream()
                .filter(member -> Objects.equals(member.getStatus(), ChatGroupUserStatus.NORMAL.getCode()))
                .collect(Collectors.toList());
        vo.setMemberCount(groupUsers.size());

        Map<Long, Integer> roleByMemberId = groupUsers.stream()
                .collect(Collectors.toMap(ChatGroupUser::getMemberId, ChatGroupUser::getRole, (left, right) -> left));

        Set<Long> memberIdList = groupUsers.stream()
                .map(ChatGroupUser::getMemberId).collect(Collectors.toSet());
        List<User> memberList = userRepository.findExistByIds(memberIdList);
        List<GroupMemberVO> groupMemberVOList = new ArrayList<>();
        for (User member : memberList) {
            GroupMemberVO groupMemberVO = new GroupMemberVO();
            groupMemberVO.setMemberId(member.getId());
            groupMemberVO.setNickname(member.getNickname());
            Integer role = roleByMemberId.get(member.getId());
            groupMemberVO.setRole(role == null ? GroupRole.MEMBER.getCode() : role);
//            groupMemberVO.setAvatarFullUrl(fileService.getLatestFullUrl(
//                    FileSourceTypeConstant.USER_AVATAR,
//                    member.getId(),
//                    member.getAvatarUrl()
//            ));
            groupMemberVOList.add(groupMemberVO);
        }

        int onlineCount = (int) memberIdList.stream()
                .filter(Objects::nonNull)
                .filter(onlineUserService::isUserOnline)
                .count();
        vo.setOnlineCount(onlineCount);

        vo.setMembers(groupMemberVOList);

        return CurlResponse.success(vo);
    }

    /**
     * 解散群聊：更新群状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<String> dissolveGroup(Long groupId) {
        User user = checkUserIsExists();
        ChatGroup group = chatGroupRepository.findNormalById(groupId);
        VerifyUtil.isTrue(group == null,ErrorCode.GROUP_NOT_EXISTS);
        VerifyUtil.isTrue(!Objects.equals(group.getOwnerId(), user.getId()), ErrorCode.UNAUTHORIZED);

        group.setStatus(ChatGroupStatus.DISSOLVED.getCode());
        VerifyUtil.ensureOperationSucceeded(chatGroupRepository.update(group), ErrorCode.GROUP_SAVE_FAIL);

        List<ChatGroupUser> groupUsers = chatGroupUserRepository.findByGroupId(groupId);
        for (ChatGroupUser member : groupUsers) {
            if (member == null) {
                continue;
            }
            member.setStatus(ChatGroupUserStatus.REMOVED.getCode());
            VerifyUtil.ensureOperationSucceeded(chatGroupUserRepository.update(member), ErrorCode.GROUP_SAVE_FAIL);
        }

        return CurlResponse.success("群聊解散成功");
    }

    /**
     * 移除群成员（仅群主/管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<String> removeMembers(RemoveGroupMemberDTO dto) {
        User currentUser = checkUserIsExists();
        Long groupId = dto.getGroupId();
        ChatGroup group = chatGroupRepository.findNormalById(groupId);
        VerifyUtil.isTrue(group == null, ErrorCode.GROUP_NOT_EXISTS);

        List<ChatGroupUser> groupUsers = chatGroupUserRepository.findByGroupId(groupId);
        Map<Long, ChatGroupUser> relationByMember = groupUsers.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(ChatGroupUser::getMemberId, g -> g, (l, r) -> l));

        ChatGroupUser operatorRelation = relationByMember.get(currentUser.getId());
        VerifyUtil.isTrue(operatorRelation == null, ErrorCode.UNAUTHORIZED);
        GroupRole operatorRole = GroupRole.fromCode(operatorRelation.getRole());
        VerifyUtil.isTrue(operatorRole == null, ErrorCode.UNAUTHORIZED);
        boolean operatorIsOwner = GroupRole.OWNER.equals(operatorRole);
        boolean operatorIsAdmin = GroupRole.ADMIN.equals(operatorRole);
        VerifyUtil.isTrue(!operatorIsOwner && !operatorIsAdmin, ErrorCode.UNAUTHORIZED);

        List<ChatGroupUser> toUpdate = new ArrayList<>();
        for (Long memberId : dto.getMemberIds()) {
            if (memberId == null) {
                continue;
            }
            VerifyUtil.isTrue(Objects.equals(memberId, currentUser.getId()), "不能移除自己");
            ChatGroupUser target = relationByMember.get(memberId);
            if (target == null || !Objects.equals(target.getStatus(), ChatGroupUserStatus.NORMAL.getCode())) {
                continue;
            }
            GroupRole targetRole = GroupRole.fromCode(target.getRole());
            if (!operatorIsOwner && GroupRole.OWNER.equals(targetRole)) {
                continue;
            }
            if (operatorIsAdmin && GroupRole.ADMIN.equals(targetRole)) {
                continue;
            }
            target.setStatus(ChatGroupUserStatus.REMOVED.getCode());
            toUpdate.add(target);
        }

        for (ChatGroupUser userRelation : toUpdate) {
            VerifyUtil.ensureOperationSucceeded(chatGroupUserRepository.update(userRelation), ErrorCode.GROUP_SAVE_FAIL);
        }

        return CurlResponse.success("移除成员成功");
    }

    /**
     * 邀请新成员入群（需互为好友）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<String> inviteMembers(InviteGroupMemberDTO dto) {
        User currentUser = checkUserIsExists();
        Long groupId = dto.getGroupId();
        ChatGroup group = chatGroupRepository.findNormalById(groupId);
        VerifyUtil.isTrue(group == null, ErrorCode.GROUP_NOT_EXISTS);

        List<ChatGroupUser> groupUsers = chatGroupUserRepository.findByGroupId(groupId);
        Map<Long, ChatGroupUser> relationByMember = groupUsers.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(ChatGroupUser::getMemberId, g -> g, (l, r) -> l));

        Conversation conversation = conversationRepository.findByGroupId(groupId);
        VerifyUtil.isTrue(conversation == null, ErrorCode.CONVERSATION_NOT_FOUND);

        List<ConversationUser> conversationUsers = conversationUserRepository
                .findByConversationIds(Collections.singleton(conversation.getId()));
        Map<Long, ConversationUser> convRelationByMember = conversationUsers.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(ConversationUser::getMemberId, c -> c, (l, r) -> l));

        List<ChatGroupUser> toInsertGroupUsers = new ArrayList<>();
        List<ChatGroupUser> toUpdateGroupUsers = new ArrayList<>();
        List<ConversationUser> toInsertConversationUsers = new ArrayList<>();
        List<ConversationUser> toUpdateConversationUsers = new ArrayList<>();

        // 构建互为好友的成员集合，避免循环内频繁查库
        Set<Long> mutualFriendIds = loadMutualFriendIds(currentUser.getId());

        long nowSeconds = System.currentTimeMillis() / 1000;
        for (Long memberId : dto.getMemberIds()) {
            if (memberId == null) {
                continue;
            }
            if (!mutualFriendIds.contains(memberId)) {
                continue;
            }
            ChatGroupUser existing = relationByMember.get(memberId);
            if (existing != null) {
                if (Objects.equals(existing.getStatus(), ChatGroupUserStatus.NORMAL.getCode())) {
                    continue;
                }
                existing.setStatus(ChatGroupUserStatus.NORMAL.getCode());
                existing.setJoinTime(nowSeconds);
                toUpdateGroupUsers.add(existing);
            } else {
                ChatGroupUser newRelation = buildChatGroupUser(groupId, memberId, GroupRole.MEMBER);
                toInsertGroupUsers.add(newRelation);
            }

            ConversationUser convRelation = convRelationByMember.get(memberId);
            if (convRelation != null) {
                if (!Objects.equals(convRelation.getStatus(), ConversationStatus.NORMAL.getCode())) {
                    convRelation.setStatus(ConversationStatus.NORMAL.getCode());
                    toUpdateConversationUsers.add(convRelation);
                }
            } else {
                ConversationUser newConvRelation = buildConversationUser(conversation.getId(), memberId);
                toInsertConversationUsers.add(newConvRelation);
            }
        }

        if (!toInsertGroupUsers.isEmpty()) {
            VerifyUtil.ensureOperationSucceeded(chatGroupUserRepository.saveBatch(toInsertGroupUsers), ErrorCode.GROUP_SAVE_FAIL);
        }
        for (ChatGroupUser userRelation : toUpdateGroupUsers) {
            VerifyUtil.ensureOperationSucceeded(chatGroupUserRepository.update(userRelation), ErrorCode.GROUP_SAVE_FAIL);
        }

        if (!toInsertConversationUsers.isEmpty()) {
            VerifyUtil.ensureOperationSucceeded(conversationUserRepository.saveBatch(toInsertConversationUsers), ErrorCode.CONVERSATION_USER_SAVE_FAIL);
        }
        for (ConversationUser relation : toUpdateConversationUsers) {
            VerifyUtil.ensureOperationSucceeded(conversationUserRepository.update(relation), ErrorCode.CONVERSATION_USER_UPDATE_FAIL);
        }

        return CurlResponse.success("邀请成功");
    }

    private Set<Long> loadMutualFriendIds(Long userId) {
        Set<Long> outgoing = friendRelationRepository.getFriendRelationByUserId(userId).stream()
                .filter(Objects::nonNull)
                .map(FriendRelation::getFriendId)
                .collect(Collectors.toSet());

        Set<Long> incoming = friendRelationRepository.getFriendRelationByFriendId(userId).stream()
                .filter(rel -> rel != null && !Objects.equals(rel.getDeleted(), Deleted.HAS_DELETED.getCode()))
                .map(FriendRelation::getUserId)
                .collect(Collectors.toSet());

        outgoing.retainAll(incoming);
        return outgoing;
    }

    /**
     * 编辑群聊信息（名称、简介、公告）
     */
    @Override
    public CurlResponse<String> editGroup(EditGroupDTO dto) {
        User user = checkUserIsExists();
        ChatGroup group = chatGroupRepository.findNormalById(dto.getGroupId());
        VerifyUtil.isTrue(group == null, ErrorCode.GROUP_NOT_EXISTS);
        VerifyUtil.isTrue(!Objects.equals(group.getOwnerId(), user.getId()), ErrorCode.UNAUTHORIZED);
        EditGroupDTO.EditGroupDTOMapper.INSTANCE.update(group, dto);

        VerifyUtil.ensureOperationSucceeded(chatGroupRepository.update(group), ErrorCode.GROUP_SAVE_FAIL);
        return CurlResponse.success("群聊信息已更新");
    }

    private ConversationUser buildConversationUser(Long conversationId, Long memberId) {
        ConversationUser conversationUser = new ConversationUser();
        conversationUser.setConversationId(conversationId);
        conversationUser.setMemberId(memberId);
        conversationUser.setStatus(ConversationStatus.NORMAL.getCode());
        return conversationUser;
    }

    private ChatGroupUser buildChatGroupUser(Long groupId, Long memberId, GroupRole role) {
        ChatGroupUser chatGroupUser = new ChatGroupUser();
        chatGroupUser.setGroupId(groupId);
        chatGroupUser.setMemberId(memberId);
        chatGroupUser.setRole(role.getCode());
        chatGroupUser.setJoinTime(System.currentTimeMillis()/1000);
        chatGroupUser.setStatus(ChatGroupUserStatus.NORMAL.getCode());
        return chatGroupUser;
    }

    private void checkFriendRelation(Long userId, List<Long> memberIds) {
        int count = friendRelationRepository.countFriendRelationByUserIdAndFriends(userId,memberIds);
        VerifyUtil.isFalse(count == memberIds.size(),ErrorCode.FRIEND_RELATION_NOT_EXISTS);
    }

    private User checkUserIsExists(){
        return currentUserAccessor.getCurrentUser();
    }

    private GroupListVO buildGroupListVO(ChatGroup chatGroup,
                                         Map<Long, Integer> roleByGroup) {
        if (chatGroup == null) {
            return null;
        }
        GroupListVO vo = GroupListVO.GroupListVOMapper.INSTANCE.toVO(chatGroup);
//        vo.setGroupAvatarFullUrl(fileService.getLatestFullUrl(
//                FileSourceTypeConstant.GROUP_AVATAR,
//                chatGroup.getId(),
//                chatGroup.getGroupAvatarUrl()
//        ));
        vo.setRole(roleByGroup.getOrDefault(chatGroup.getId(), GroupRole.MEMBER.getCode()));
        return vo;
    }

}
