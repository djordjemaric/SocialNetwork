package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestStatus;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.entity.*;
import com.socialnetwork.socialnetwork.exceptions.BusinessLogicException;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.mapper.PostMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.repository.GroupRequestRepository;
import com.socialnetwork.socialnetwork.repository.PostRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.socialnetwork.socialnetwork.exceptions.ErrorCode.*;


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

    public GroupDTO createGroup(CreateGroupDTO group) throws BusinessLogicException {
        User currentUser = jwtService.getUser();

        if (groupRepository.existsByName(group.name())) {
            throw new BusinessLogicException(ERROR_CREATING_GROUP, "Group with that name already exists.");
        }
        Group createdGroup = groupRepository.save(groupMapper.dtoToEntity(currentUser, group));

        groupMemberRepository.save(new GroupMember(null, currentUser, createdGroup));

        return groupMapper.entityToGroupDto(createdGroup);
    }


    public List<PostDTO> getAllPostsByGroupId(Integer idGroup) throws ResourceNotFoundException, BusinessLogicException {
        User currentUser = jwtService.getUser();

        if (!groupRepository.existsById(idGroup)) {
            throw new BusinessLogicException(ERROR_GETTING_GROUP_POSTS, "Group with id "
                    + idGroup + "does not exist.");
        }

        if (!groupMemberRepository.existsByUserIdAndGroupId(currentUser.getId(), idGroup)) {
            throw new ResourceNotFoundException(ERROR_GETTING_GROUP_POSTS, "User " + currentUser.getEmail()
                     + " is not a member of the group with id: " + idGroup);

        }

        List<Post> posts = postRepository.findAllByGroup_Id(idGroup);

        return posts.stream()
                .map(postMapper::postToPostDTO)
                .toList();
    }

    public void deleteGroup(Integer idGroup) throws ResourceNotFoundException {
        User currentUser = jwtService.getUser();

        if (!groupRepository.existsByIdAndAdminId(idGroup, currentUser.getId())) {
            throw new ResourceNotFoundException(ERROR_DELETING_GROUP,
                    "You can't delete a group that you are not an admin of.");
        }

        groupRepository.deleteById(idGroup);
    }

    public List<GroupDTO> findByName(String name) {
        List<Group> groups = groupRepository.findAllByNameStartingWith(name);

        return groups.stream().map(group ->
                new GroupDTO(group.getName(), group.getAdmin().getEmail(), group.isPublic(), group.getId())).toList();
    }

    public ResolvedGroupRequestDTO createRequestToJoinGroup(Integer idGroup) throws BusinessLogicException, ResourceNotFoundException {
        User currentUser = jwtService.getUser();

        Group group = groupRepository.findById(idGroup).orElseThrow(() ->
               new ResourceNotFoundException(ERROR_CREATING_REQUEST_GROUP, "Group with id "
                    + idGroup + "does not exist"));

        if (groupRequestRepository.existsByUserIdAndGroupId(currentUser.getId(), idGroup)) {
            throw new BusinessLogicException(ERROR_CREATING_REQUEST_GROUP, "The request has already been sent.");
        }

        if (groupMemberRepository.existsByUserIdAndGroupId(currentUser.getId(), idGroup)) {
            throw new BusinessLogicException(ERROR_CREATING_REQUEST_GROUP, "You are already member of that group.");
        }

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

    public void leaveGroup(Integer idGroup) throws BusinessLogicException, ResourceNotFoundException {
        Group group = groupRepository.findById(idGroup).orElseThrow(() ->
                new ResourceNotFoundException(ERROR_LEAVING_GROUP, "Group with id "
                    + idGroup + "does not exist"));
        User user = jwtService.getUser();
        if (group.getAdmin().getId().equals(user.getId())) {
            throw new BusinessLogicException(ERROR_LEAVING_GROUP, "Admin can't leave a group.");
        }
        GroupMember groupMember = groupMemberRepository.findByMember(user).orElseThrow(() ->
                new ResourceNotFoundException(ERROR_LEAVING_GROUP, "You are not a member of that group!"));
        groupMemberRepository.delete(groupMember);
    }

    @Transactional
    public void removeMember(Integer idGroup, Integer idUser) throws BusinessLogicException, ResourceNotFoundException {
        User admin = jwtService.getUser();
        if (!groupRepository.existsByAdminIdAndGroupId(admin.getId(), idGroup)) {
            throw new AccessDeniedException("You can't remove a member from the group that you are not an admin of.");
        }
        if (admin.getId().equals(idUser)) {
            throw new BusinessLogicException(ERROR_REMOVING_MEMBER, "Admin can not leave a group.");
        }
        if (!groupMemberRepository.existsByUserIdAndGroupId(idUser, idGroup)) {
            throw new ResourceNotFoundException(ERROR_REMOVING_MEMBER, "User with an id: " + idUser
                     + " is not a member of the group with id: " + idGroup);
        }
        groupMemberRepository.deleteGroupMemberByGroupIdAndMemberId(idGroup, idUser);
    }


}
