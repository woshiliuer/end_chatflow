package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.group.AddGroupDTO;
import org.example.chatflow.model.vo.GroupListTotalVO;

import java.util.List;

/**
 * @author by zzr
 */
public interface GroupService {
    CurlResponse<String> addGroup(AddGroupDTO dto);

    CurlResponse<List<GroupListTotalVO>> groupList();
}
