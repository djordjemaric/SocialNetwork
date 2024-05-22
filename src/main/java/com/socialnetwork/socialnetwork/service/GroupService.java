package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.CreateGroupDto;
import com.socialnetwork.socialnetwork.dto.GroupDto;
import com.socialnetwork.socialnetwork.dto.GroupRequest_MemberDto;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.repository.GroupRequestRepository;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupRequestRepository groupRequestRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final GroupMapper groupMapper;
    private final JwtService jwtService;

    public GroupService(GroupRepository groupRepository, GroupMemberRepository groupMemberRepository, GroupMapper groupMapper, GroupRequestRepository groupRequestRepository, UserRepository userRepository, JwtService jwtService) {
        this.groupRepository = groupRepository;
        this.groupRequestRepository = groupRequestRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
        this.groupMapper = groupMapper;
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

    public GroupRequest_MemberDto createRequestToJoinGroup(Integer idGroup) {
        User currentUser = jwtService.getUser();

        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist!"));

        if (group.isPublic()) {
            return addUserAsAMemberToPublicGroup(idGroup);
        }

        //provera da li je user vec u toj grupi
        if (groupMemberRepository.existsByUserIdAndGroupId(currentUser.getId(), idGroup)) {
            throw new FunctionArgumentException("User is already in that group");
        }

        GroupRequest groupRequest = groupRequestRepository.save(new GroupRequest(null, currentUser, group));


        return new GroupRequest_MemberDto(groupRequest.getId(), currentUser, group, "Created request!");
    }

    public GroupRequest_MemberDto addUserAsAMemberToPublicGroup(Integer idGroup) {
        User currentUser = jwtService.getUser();

        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist!"));

        //provera da li je user vec u toj grupi
        if (groupMemberRepository.existsByUserIdAndGroupId(currentUser.getId(), idGroup)) {
            throw new FunctionArgumentException("User is already in that group");
        }
        GroupMember groupMember = groupMemberRepository.save(new GroupMember(null, currentUser, group));


        return new GroupRequest_MemberDto(groupMember.getId(), currentUser, group, "User added as a member!");

    }

}
