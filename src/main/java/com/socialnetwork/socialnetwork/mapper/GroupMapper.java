package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
import com.socialnetwork.socialnetwork.dto.group.GroupDto;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.stereotype.Component;


@Component
public class GroupMapper {
    public Group dtoToEntity(User user, CreateGroupDto createGroupDto) {
        Group group = new Group();
        group.setPublic(createGroupDto.isPublic());
        group.setAdmin(user);
        group.setName(createGroupDto.name());
        return group;
    }

    public GroupDto entityToGroupDto(Group group) {
        return new GroupDto(group.getName(),
                group.getAdmin().getEmail(),
                group.isPublic(),
                group.getId());
    }
}
