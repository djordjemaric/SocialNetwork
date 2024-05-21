package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.group.GroupDto;
import com.socialnetwork.socialnetwork.entity.Group;
import org.springframework.stereotype.Component;


@Component
public class GroupMapper {
    public GroupDto entityToGroupDto(Group group) {
        return new GroupDto(group.getName(),
                group.getAdmin().getEmail(),
                group.isPublic(),
                group.getId());
    }
}
