package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.stereotype.Component;


@Component
public class GroupMapper {
    public Group dtoToEntity(User user, CreateGroupDTO createGroupDto) {
        Group group = new Group();
        group.setPublic(createGroupDto.isPublic());
        group.setAdmin(user);
        group.setName(createGroupDto.name());
        return group;
    }

    public GroupDTO entityToGroupDto(Group group) {
        return new GroupDTO(group.getName(),
                group.getAdmin().getEmail(),
                group.isPublic(),
                group.getId());
    }

    public GroupDTO requestToGroupDTO(GroupRequest request) {
        return new GroupDTO(request.getGroup().getName(),
                request.getGroup().getAdmin().getEmail(),
                request.getGroup().isPublic(),
                request.getGroup().getId());
    }

}
