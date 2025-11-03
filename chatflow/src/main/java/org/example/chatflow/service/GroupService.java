package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.group.AddGroupDTO;
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
}
