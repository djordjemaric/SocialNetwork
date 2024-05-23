package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.group.*;
import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.mapper.GroupRequestMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.GroupRequestRepository;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.repository.UserRepository;
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
    private final GroupRequestMapper groupRequestMapper;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public GroupService(GroupRepository groupRepository, GroupRequestRepository groupRequestRepository, JwtService jwtService, GroupMemberRepository groupMemberRepository, GroupMapper groupMapper, GroupRequestMapper groupRequestMapper, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.groupRequestRepository = groupRequestRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupMapper = groupMapper;
        this.jwtService = jwtService;
        this.groupRequestMapper = groupRequestMapper;
        this.userRepository = userRepository;
    }

    public GroupDTO createGroup(CreateGroupDTO group) {
        User currentUser = jwtService.getUser();

        if (groupRepository.existsByName(group.name())) {
            throw new FunctionArgumentException("Group with that name already exists");
        }
        Group createdGroup = groupRepository.save(groupMapper.dtoToEntity(currentUser, group));

        groupMemberRepository.save(new GroupMember(null, currentUser, createdGroup));

        return groupMapper.entityToGroupDto(createdGroup);
    }

    public void deleteGroup(Integer idGroup) {

        User currentUser = jwtService.getUser();

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


    public List<GroupRequestDTO> getAllRequestsForGroup(Integer idGroup) {
        User currentUser = jwtService.getUser();
        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist!"));

        if (!groupRepository.existsByAdmin(currentUser)) {
            throw new FunctionArgumentException("There is no group with given admin id!");
        }
        List<GroupRequest> groupRequests = groupRequestRepository.findAllByGroup(group);

        return groupRequests.stream()
                .map(groupRequestMapper::requestToGroupRequestDTO)
                .toList();

    }

    public RequestDTO checkRequest(Integer idGroup, Integer idRequest) {

        User currentUser = jwtService.getUser();

        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist!"));
        GroupRequest request = groupRequestRepository.findById(idRequest).orElseThrow(() -> new FunctionArgumentException("Request does not exist!"));
        User newMember = userRepository.findById(request.getUser().getId()).orElseThrow(() -> new FunctionArgumentException("User with that id does not exist!"));

        if (!group.getAdmin().equals(currentUser)) {
            throw new FunctionArgumentException("That user is not an admin for that group!");
        }

        if (!groupRequestRepository.existsByGroupId(idGroup)) {
            throw new FunctionArgumentException("That request does not exist");
        }

        if (!groupRequestRepository.existsByUserAndGroup(newMember, group)) {
            throw new FunctionArgumentException("That request does not exist");
        }

        if (group.isPublic()) {
            throw new FunctionArgumentException("Given group is public");
        }

        return new RequestDTO(newMember, group, request);
    }

    public void acceptRequest(Integer idGroup, Integer idRequest) {

        RequestDTO requestDTO = checkRequest(idGroup, idRequest);

        groupMemberRepository.save(new GroupMember(null, requestDTO.user(), requestDTO.group()));
        groupRequestRepository.deleteById(idRequest);
    }

    public void rejectRequest(Integer idGroup, Integer idRequest) {
        RequestDTO requestDTO = checkRequest(idGroup, idRequest);

        groupRequestRepository.deleteById(requestDTO.groupRequest().getId());
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
