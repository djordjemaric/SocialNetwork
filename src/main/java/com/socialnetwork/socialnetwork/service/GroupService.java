package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.group.*;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.entity.*;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.mapper.GroupRequestMapper;
import com.socialnetwork.socialnetwork.mapper.PostMapper;
import com.socialnetwork.socialnetwork.repository.*;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;


@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupRequestRepository groupRequestRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final GroupMapper groupMapper;
    private final PostMapper postMapper;
    private final GroupRequestMapper groupRequestMapper;
    private final JwtService jwtService;

    public GroupService(GroupRepository groupRepository, GroupRequestRepository groupRequestRepository, PostRepository postRepository, UserRepository userRepository, GroupMapper groupMapper, GroupMemberRepository groupMemberRepository, PostMapper postMapper, GroupRequestMapper groupRequestMapper, JwtService jwtService) {
        this.groupRepository = groupRepository;
        this.groupRequestRepository = groupRequestRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupMapper = groupMapper;
        this.postMapper = postMapper;
        this.groupRequestMapper = groupRequestMapper;
        this.jwtService = jwtService;
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


    public List<PostDTO> getAllPostsByGroupId(Integer idGroup) {
        User currentUser = jwtService.getUser();

        if (!groupRepository.existsById(idGroup)) {
            throw new FunctionArgumentException("Group with that id does not exists");
        }

        if (!groupMemberRepository.existsByUserIdAndGroupId(currentUser.getId(), idGroup)) {
            throw new FunctionArgumentException("User is not member of give group!");
        }

        List<Post> posts = postRepository.findAllByGroup_Id(idGroup);

        return posts.stream()
                .map(postMapper::postToPostDTO)
                .toList();
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

        return groups.stream().map(groupMapper::entityToGroupDto).toList();
    }


    public ResolvedGroupRequestDTO createRequestToJoinGroup(Integer idGroup) {
        User currentUser = jwtService.getUser();

        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist!"));

        if (groupRequestRepository.existsByUserIdAndGroupId(currentUser.getId(), idGroup)) {
            throw new FunctionArgumentException("Request already exist");
        }

        if (groupMemberRepository.existsByUserIdAndGroupId(currentUser.getId(), idGroup)) {
            throw new FunctionArgumentException("User is already in that group");
        }

        if (group.isPublic()) {
            return addUserAsAMemberToPublicGroup(currentUser, group);
        }

        return addUserAsAMemberToPrivateGroup(currentUser, group);
    }

    public ResolvedGroupRequestDTO addUserAsAMemberToPrivateGroup(User user, Group group) {
        GroupRequest groupRequest = groupRequestRepository.save(new GroupRequest(null, user, group));

        return groupRequestMapper.requestToResolvedGroupRequestDTOStatusCreated(groupRequest);
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

    public GroupRequest checkRequest(Integer idGroup, Integer idRequest) {
        User currentUser = jwtService.getUser();

        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist!"));
        GroupRequest request = groupRequestRepository.findById(idRequest).orElseThrow(() -> new FunctionArgumentException("Request does not exist!"));
        User newMember = userRepository.findById(request.getUser().getId()).orElseThrow(() -> new FunctionArgumentException("User with that id does not exist!"));

        if (!groupRepository.existsByAdmin(currentUser)) {
            throw new FunctionArgumentException("That user is not an admin for that group!");
        }

        if (!groupRequestRepository.existsByUserAndGroup(newMember, group)) {
            throw new FunctionArgumentException("That request does not exist");
        }

        if (group.isPublic()) {
            throw new FunctionArgumentException("Given group is public");
        }

        return request;
    }

    public void acceptRequest(Integer idGroup, Integer idRequest) {
         GroupRequest groupRequest = checkRequest(idGroup, idRequest);

        groupMemberRepository.save(new GroupMember(null, groupRequest.getUser(), groupRequest.getGroup()));
        groupRequestRepository.deleteById(idRequest);
    }

    public void rejectRequest(Integer idGroup, Integer idRequest) {
        GroupRequest groupRequest = checkRequest(idGroup, idRequest);

        groupRequestRepository.deleteById(groupRequest.getId());
    }

    public ResolvedGroupRequestDTO addUserAsAMemberToPublicGroup(User user, Group group) {
        GroupMember groupMember = groupMemberRepository.save(new GroupMember(null, user, group));

        return groupRequestMapper.groupMemberToResolvedGroupRequestDTOStatusAccepted(groupMember);
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
            throw new NoSuchElementException("There are no groups with that id: " + idGroup + " and that admin: " + admin.getEmail());
        }
        if (admin.getId().equals(idUser)) {
            throw new RuntimeException("Can't remove an admin from the group!");
        }
        if (!groupMemberRepository.existsByUserIdAndGroupId(idUser, idGroup)) {
            throw new NoSuchElementException("User with that id: " + idUser + " is not in this group: " + idGroup);
        }
        groupMemberRepository.deleteGroupMemberByGroupIdAndMemberId(idGroup, idUser);
    }


}
