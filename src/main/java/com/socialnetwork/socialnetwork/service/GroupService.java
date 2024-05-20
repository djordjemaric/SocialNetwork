package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.GroupRequestRepository;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.stereotype.Service;
import com.socialnetwork.socialnetwork.repository.GroupRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupRequestRepository groupRequestRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMapper groupMapper;

    private final UserRepository userRepository;
    private final JwtService jwtService;


    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper, GroupMemberRepository groupMemberRepository, GroupRequestRepository groupRequestRepository, UserRepository userRepository, JwtService jwtService) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.groupRequestRepository = groupRequestRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
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
