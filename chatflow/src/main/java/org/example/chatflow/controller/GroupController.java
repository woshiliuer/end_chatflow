package org.example.chatflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.entity.Param;
import org.example.chatflow.model.dto.group.AddGroupDTO;
import org.example.chatflow.model.dto.group.EditGroupDTO;
import org.example.chatflow.model.dto.group.InviteGroupMemberDTO;
import org.example.chatflow.model.dto.group.RemoveGroupMemberDTO;
import org.example.chatflow.model.vo.GroupListTotalVO;
import org.example.chatflow.model.vo.GroupDetailVO;
import org.example.chatflow.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author by zzr
 */
@RestController
@RequestMapping("/group")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GroupController {

    private final GroupService groupService;

    @Operation(description = "新建群聊")
    @PostMapping("/addGroup")
    public CurlResponse<String> addGroup(@RequestBody @Validated AddGroupDTO dto) {
        return groupService.addGroup(dto);
    }

    @Operation(description = "群聊列表")
    @PostMapping("/groupList")
    public CurlResponse<List<GroupListTotalVO>>  groupList(){
        return groupService.groupList();
    }

    @Operation(description = "群聊详情")
    @PostMapping("/groupDetail")
    public CurlResponse<GroupDetailVO> groupDetail(@RequestBody @Validated Param<Long> param) {
        return groupService.groupDetail(param.getParam());
    }

    @Operation(description = "解散群聊")
    @PostMapping("/dissolve")
    public CurlResponse<String> dissolve(@RequestBody @Validated Param<Long> param) {
        return groupService.dissolveGroup(param.getParam());
    }

    @Operation(description = "编辑群聊")
    @PostMapping("/edit")
    public CurlResponse<String> edit(@RequestBody @Validated EditGroupDTO dto) {
        return groupService.editGroup(dto);
    }

    @Operation(description = "移除群成员")
    @PostMapping("/removeMembers")
    public CurlResponse<String> removeMembers(@RequestBody @Validated RemoveGroupMemberDTO dto) {
        return groupService.removeMembers(dto);
    }

    @Operation(description = "邀请新成员")
    @PostMapping("/inviteMembers")
    public CurlResponse<String> inviteMembers(@RequestBody @Validated InviteGroupMemberDTO dto) {
        return groupService.inviteMembers(dto);
    }

}
