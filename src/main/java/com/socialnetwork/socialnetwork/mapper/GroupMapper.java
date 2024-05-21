package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.group.GroupMemberDto;
import com.socialnetwork.socialnetwork.dto.group.GroupRequestDto;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import org.springframework.stereotype.Component;


@Component
public class GroupMapper {

    public GroupMemberDto groupMemberToGroupMemberDto (GroupMember groupMember){

        return new GroupMemberDto(groupMember.getMember().getEmail(),
                groupMember.getGroup().getName(),
                groupMember.getMember().getId(),
                groupMember.getGroup().getId());
    }

    public GroupRequestDto groupRequestToGroupRequestDto (GroupRequest groupRequest){

        return new GroupRequestDto(groupRequest.getUser().getEmail(),
                groupRequest.getGroup().getName(),
                groupRequest.getUser().getId(),
                groupRequest.getGroup().getId());
    }

}
