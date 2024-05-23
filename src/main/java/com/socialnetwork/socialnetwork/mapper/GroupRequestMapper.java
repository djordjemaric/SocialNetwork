package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupRequestDTO;
import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import org.springframework.stereotype.Component;

@Component

public class GroupRequestMapper {

    private final UserMapper userMapper;
    private final GroupMapper groupMapper;

    public GroupRequestMapper(UserMapper userMapper, GroupMapper groupMapper) {
        this.userMapper = userMapper;
        this.groupMapper = groupMapper;
    }

    public GroupRequestDTO requestToGroupRequestDTO(GroupRequest request) {

        PreviewUserDTO userDTO = userMapper.requestToPreviewUserDTO(request);
        GroupDTO groupDTO = groupMapper.requestToGroupDTO(request);

        return new GroupRequestDTO(userDTO, groupDTO, request.getId());
    }
}
