package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.GroupDto;
import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMapper groupMapper;

    private final JwtService jwtService;

    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper, GroupMemberRepository groupMemberRepository, JwtService jwtService) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.groupMemberRepository = groupMemberRepository;
        this.jwtService = jwtService;
    }

    public GroupDto createGroup(CreateGroupDto group) {
        User currentUser = jwtService.getUser();

        //provera da li postoji grupa sa tim imenom
        if (groupRepository.existsByName(group.name())) {
            throw new FunctionArgumentException("Group with that name already exists");
        }

        //kreiranje grupe
        Group createdGroup = groupRepository.save(groupMapper.dtoToEntity(currentUser, group));

        //dodavanje admina kao membera u tu grupu
        groupMemberRepository.save(new GroupMember(null, currentUser, createdGroup));

        return groupMapper.entityToGroupDto(createdGroup);
    }

     public void removeMember (Integer idGroup, Integer idUser){

        // need to check if the group with that admin and group id exists
        // need to check if the user is in the group and if he is the admin
        // need to remove user from GroupMember table
        User admin = jwtService.getUser();
        if (groupRepository.existsByAdminIdAndGroupId(admin.getId(), idGroup)) {
            if (groupMemberRepository.existsByUserIdAndGroupId(idUser, idGroup)) {
                if (!Objects.equals(admin.getId(), idUser)){
                    groupMemberRepository.deleteGroupMemberByGroupIdAndMemberId(idGroup, idUser);
                } else {
                    throw new FunctionArgumentException("Can't remove an admin from the group!");
                }
            } else {
                throw new FunctionArgumentException("User with that is not in this group");
            }
        } else {
            throw new FunctionArgumentException("There are no groups with that tuple (IdAdmin, IdGroup)");
        }
    }


}
