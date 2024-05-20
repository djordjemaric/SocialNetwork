package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GroupMapper {


    public GroupRequest createGroupRequestEntity (User user, Group group){

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setUser(user);
        groupRequest.setGroup(group);


        return groupRequest;
    }


    public GroupMember createGroupMemberEntity (User user, Group group){

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(user);
        groupMember.setGroup(group);


        return groupMember;
    }

}
