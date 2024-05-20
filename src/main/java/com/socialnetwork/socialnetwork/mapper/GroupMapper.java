package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

    public Group createDtoToEntity (Integer idUser, CreateGroupDto createGroupDto){

            //dodaj instancu usera
            User usr = new User();
            usr.setId(idUser);

            Group group = new Group();
            group.setPublic(createGroupDto.isPublic());
            group.setAdmin(usr);
            group.setName(createGroupDto.name());
        return group;
    }

    public GroupRequest createGroupRequestEntity (Integer idUser, Group group){

        User user = new User();
        user.setId(idUser);

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setUser(user);
        groupRequest.setGroup(group);


        return groupRequest;
    }


    public GroupMember createGroupMemberEntity (Integer idUser, Group group){

        User user = new User();
        user.setId(idUser);

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(user);
        groupMember.setGroup(group);


        return groupMember;
    }

}
