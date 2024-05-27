package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupRequestDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestStatus;
import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.entity.GroupMember;
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
        PreviewUserDTO userDTO = userMapper.userToPreviewUserDTO(request.getUser());
        GroupDTO groupDTO = groupMapper.groupToGroupDTO(request.getGroup());

        return new GroupRequestDTO(userDTO, groupDTO, request.getId());
    }

    public ResolvedGroupRequestDTO requestToResolvedGroupRequestDTOStatusCreated(GroupRequest request) {
        PreviewUserDTO userDTO = userMapper.userToPreviewUserDTO(request.getUser());
        GroupDTO groupDTO = groupMapper.groupToGroupDTO(request.getGroup());

        return new ResolvedGroupRequestDTO(request.getId(), userDTO, groupDTO,
                ResolvedGroupRequestStatus.REQUEST_TO_JOIN_GROUP_CREATED);
    }

    public ResolvedGroupRequestDTO groupMemberToResolvedGroupRequestDTOStatusAccepted(GroupMember groupMember) {
        PreviewUserDTO userDTO = userMapper.userToPreviewUserDTO(groupMember.getMember());
        GroupDTO groupDTO = groupMapper.groupToGroupDTO(groupMember.getGroup());

        return new ResolvedGroupRequestDTO(groupMember.getId(), userDTO, groupDTO,
                ResolvedGroupRequestStatus.REQUEST_TO_JOIN_GROUP_ACCEPTED);
    }
}
