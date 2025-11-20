from pathlib import Path
path = Path('src/main/java/org/example/chatflow/service/impl/GroupServiceImpl.java')
text = path.read_text(encoding='utf-8')
start = text.find('public CurlResponse<String> addGroup')
end = text.find('public CurlResponse<List<GroupListTotalVO>> groupList')
if start == -1 or end == -1:
    raise SystemExit('marker not found')
new_method = '''    @Transactional(rollbackFor = Exception.class)
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
        VerifyUtil.ensureOperationSucceeded(chatGroupRepository.save(chatGroup), ErrorCode.GROUP_SAVE_FAIL);

        //创建会话
        Conversation conversation = new Conversation();
        conversation.setConversationType(ConversationType.GROUP.getCode());
        conversation.setGroupId(chatGroup.getId());
        VerifyUtil.ensureOperationSucceeded(conversationRepository.save(conversation), ErrorCode.CONVERSATION_SAVE_FAIL);

        //验证是否所有成员都有好友关系
        checkFriendRelation(user.getId(), dto.getMemberIds());

        //添加会话，用户关系
        Long conversationId = conversation.getId();
        List<ConversationUser> conversationUserList = new ArrayList<>();
        conversationUserList.add(buildConversationUser(conversationId, user.getId(), GroupRole.OWNER));
        for (Long memberId : dto.getMemberIds()) {
            conversationUserList.add(buildConversationUser(conversationId, memberId, GroupRole.MEMBER));
        }
        VerifyUtil.ensureOperationSucceeded(
                conversationUserRepository.saveBatch(conversationUserList),
                ErrorCode.CONVERSATION_USER_SAVE_FAIL
        );

        // 创建群聊成员关系
        List<ChatGroupUser> groupUsers = new ArrayList<>();
        long joinTime = System.currentTimeMillis() / 1000;
        groupUsers.add(buildChatGroupUser(chatGroup.getId(), user.getId(), GroupRole.OWNER, joinTime));
        for (Long memberId : dto.getMemberIds()) {
            groupUsers.add(buildChatGroupUser(chatGroup.getId(), memberId, GroupRole.MEMBER, joinTime));
        }
        VerifyUtil.ensureOperationSucceeded(chatGroupUserRepository.saveBatch(groupUsers), ErrorCode.GROUP_SAVE_FAIL);

        return CurlResponse.success("群聊创建成功");
    }
'''
path.write_text(text[:start] + new_method + text[end:], encoding='utf-8')
