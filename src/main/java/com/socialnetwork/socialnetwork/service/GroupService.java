package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestStatus;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.entity.*;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.mapper.PostMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.repository.GroupRequestRepository;
import com.socialnetwork.socialnetwork.repository.PostRepository;
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
    private final GroupMapper groupMapper;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final JwtService jwtService;

    public GroupService(GroupRepository groupRepository, JwtService jwtService, GroupRequestRepository groupRequestRepository, GroupMemberRepository groupMemberRepository, GroupMapper groupMapper, PostMapper postMapper, PostRepository postRepository) {
        this.groupRepository = groupRepository;
        this.groupRequestRepository = groupRequestRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupMapper = groupMapper;
        this.jwtService = jwtService;
        this.postMapper = postMapper;
        this.postRepository = postRepository;
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

        return groups.stream().map(group -> new GroupDTO(group.getName(), group.getAdmin().getEmail(), group.isPublic(), group.getId())).toList();
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

        return new ResolvedGroupRequestDTO(groupRequest.getId(), new PreviewUserDTO(user.getId(), user.getEmail()), new GroupDTO(group.getName(), group.getAdmin().getEmail(), group.isPublic(), group.getId()), ResolvedGroupRequestStatus.REQUEST_TO_JOIN_GROUP_CREATED);
    }

    public ResolvedGroupRequestDTO addUserAsAMemberToPublicGroup(User user, Group group) {
        GroupMember groupMember = groupMemberRepository.save(new GroupMember(null, user, group));

        return new ResolvedGroupRequestDTO(groupMember.getId(), new PreviewUserDTO(user.getId(), user.getEmail()), new GroupDTO(group.getName(), group.getAdmin().getEmail(), group.isPublic(), group.getId()), ResolvedGroupRequestStatus.REQUEST_TO_JOIN_GROUP_ACCEPTED);
    }

    public void leaveGroup(Integer idGroup) {
        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist"));
        User user = jwtService.getUser();
        if (group.getAdmin().getId().equals(user.getId())) {
            throw new FunctionArgumentException("Admin can't leave the group");
        }
        GroupMember groupMember = groupMemberRepository.findByMemberAndGroup(user, group).orElseThrow(() -> new FunctionArgumentException("User is not member of group"));
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
