package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestStatus;
import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.repository.GroupRequestRepository;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;


import java.util.List;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupRequestRepository groupRequestRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMapper groupMapper;
    private final JwtService jwtService;

    public GroupService(GroupRepository groupRepository, GroupRequestRepository groupRequestRepository, GroupMapper groupMapper, GroupMemberRepository groupMemberRepository, JwtService jwtService) {
        this.groupRepository = groupRepository;
        this.groupRequestRepository = groupRequestRepository;
        this.groupMapper = groupMapper;
        this.groupMemberRepository = groupMemberRepository;
        this.jwtService = jwtService;
    }

    public GroupDTO createGroup(CreateGroupDTO group) {
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

    public void deleteGroup(Integer idGroup) {

        User currentUser = jwtService.getUser();

        //provera da li postoji grupa sa prosledjenim id-jem i id-jem admina
        if (!groupRepository.existsByIdAndAdminId(idGroup, currentUser.getId())) {
            throw new FunctionArgumentException("There is no group with given id or id of admin");
        }

        groupRepository.deleteById(idGroup);

    }


    public List<GroupDTO> findByName(String name) {

        List<Group> groups = groupRepository.findAllByNameStartingWith(name);

        return groups.stream()
                .map(group -> new GroupDTO(group.getName(), group.getAdmin().getEmail(), group.isPublic(), group.getId()))
                .toList();
    }


    public ResolvedGroupRequestDTO createRequestToJoinGroup(Integer idGroup) {
        User currentUser = jwtService.getUser();

        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist!"));

        //provera da li postoji taj request
        if (groupRequestRepository.existsByUserIdAndGroupId(currentUser.getId(), idGroup)) {
            throw new FunctionArgumentException("Request already exist");
        }

        //provera da li je user vec u toj grupi
        if (groupMemberRepository.existsByUserIdAndGroupId(currentUser.getId(), idGroup)) {
            throw new FunctionArgumentException("User is already in that group");
        }

        //ako je grupa public dodajemo usera
        if (group.isPublic()) {
            return addUserAsAMemberToPublicGroup(currentUser, group);
        }

        return addUserAsAMemberToPrivateGroup(currentUser, group);
    }

    public ResolvedGroupRequestDTO addUserAsAMemberToPrivateGroup(User user, Group group) {

        GroupRequest groupRequest = groupRequestRepository.save(new GroupRequest(null, user, group));

        return new ResolvedGroupRequestDTO(groupRequest.getId(),
                new PreviewUserDTO(user.getId(), user.getEmail()),
                new GroupDTO(group.getName(), group.getAdmin().getEmail(), group.isPublic(), group.getId()),
                ResolvedGroupRequestStatus.REQUEST_TO_JOIN_GROUP_CREATED);

    }

    public ResolvedGroupRequestDTO addUserAsAMemberToPublicGroup(User user, Group group) {

        GroupMember groupMember = groupMemberRepository.save(new GroupMember(null, user, group));

        return new ResolvedGroupRequestDTO(groupMember.getId(),
                new PreviewUserDTO(user.getId(), user.getEmail()),
                new GroupDTO(group.getName(), group.getAdmin().getEmail(), group.isPublic(), group.getId()),
                ResolvedGroupRequestStatus.REQUEST_TO_JOIN_GROUP_ACCEPTED);

    }

    public void leaveGroup(Integer idGroup) {
        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist"));
        User user = jwtService.getUser();
        if (group.getAdmin().getId().equals(user.getId())) {
            throw new FunctionArgumentException("Admin can't leave the group");
        }
        GroupMember groupMember = groupMemberRepository.findByMember(user).orElseThrow(() -> new FunctionArgumentException("User is not member of group"));
        groupMemberRepository.delete(groupMember);
    }

    @Transactional
    public void removeMember(Integer idGroup, Integer idUser) {

        // need to check if the group with that admin and group id exists
        // need to check if the user is in the group and if he is the admin
        // need to remove user from GroupMember table
        User admin = jwtService.getUser();
        if (!groupRepository.existsByAdminIdAndGroupId(admin.getId(), idGroup)) {
            throw new NoSuchElementException("There are no groups with that id: "
                    + idGroup + " and that admin: " + admin.getEmail());
        }
        if (admin.getId().equals(idUser)) {
            throw new RuntimeException("Can't remove an admin from the group!");
        }
        if (!groupMemberRepository.existsByUserIdAndGroupId(idUser, idGroup)) {
            throw new NoSuchElementException("User with that id: " + idUser
                    + " is not in this group: " + idGroup);
        }
        groupMemberRepository.deleteGroupMemberByGroupIdAndMemberId(idGroup, idUser);
    }


}
