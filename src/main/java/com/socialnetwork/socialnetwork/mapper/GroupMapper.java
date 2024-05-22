package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupMemberDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupRequestDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
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

    public GroupMemberDTO groupMemberToGroupMemberDto (GroupMember groupMember){

        return new GroupMemberDTO(groupMember.getMember().getEmail(),
                groupMember.getGroup().getName(),
                groupMember.getMember().getId(),
                groupMember.getGroup().getId());
    }

    public GroupRequestDTO groupRequestToGroupRequestDto (GroupRequest groupRequest){

        return new GroupRequestDTO(groupRequest.getUser().getEmail(),
                groupRequest.getGroup().getName(),
                groupRequest.getUser().getId(),
                groupRequest.getGroup().getId());
    }

}
