package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.group.AddGroupDTO;
import org.example.chatflow.model.dto.group.EditGroupDTO;
import org.example.chatflow.model.dto.group.InviteGroupMemberDTO;
import org.example.chatflow.model.dto.group.RemoveGroupMemberDTO;
import org.example.chatflow.model.vo.GroupListTotalVO;
import org.example.chatflow.model.vo.GroupDetailVO;

import java.util.List;

/**
 * @author by zzr
 */
public interface GroupService {
    CurlResponse<String> addGroup(AddGroupDTO dto);

    CurlResponse<List<GroupListTotalVO>> groupList();

    CurlResponse<GroupDetailVO> groupDetail(Long groupId);

    /**
     * 解散群聊
     * @param groupId 群聊ID
     * @return 操作结果
     */
    CurlResponse<String> dissolveGroup(Long groupId);

    /**
     * 编辑群聊
     * @param dto 编辑参数
     * @return 操作结果
     */
    CurlResponse<String> editGroup(EditGroupDTO dto);

    /**
     * 移除群成员
     * @param dto 参数
     * @return 操作结果
     */
    CurlResponse<String> removeMembers(RemoveGroupMemberDTO dto);

    /**
     * 邀请新成员入群
     * @param dto 参数
     * @return 操作结果
     */
    CurlResponse<String> inviteMembers(InviteGroupMemberDTO dto);
}
