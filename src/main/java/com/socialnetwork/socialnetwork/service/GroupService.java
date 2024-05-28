package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.group.*;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.entity.*;
import com.socialnetwork.socialnetwork.exceptions.BusinessLogicException;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.mapper.GroupRequestMapper;
import com.socialnetwork.socialnetwork.mapper.PostMapper;
import com.socialnetwork.socialnetwork.repository.*;
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
        this.jwtService = jwtService;
        this.postMapper = postMapper;
        this.groupRequestMapper = groupRequestMapper;
    }

    public GroupDTO createGroup(CreateGroupDTO group) throws BusinessLogicException, ResourceNotFoundException {
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
        Group group = groupRepository.findById(idGroup).orElseThrow(() ->
                new BusinessLogicException(ERROR_GETTING_GROUP_POSTS, "Group with id "
                        + idGroup + "does not exist"));

        if (!groupMemberRepository.existsByUserIdAndGroupId(currentUser.getId(), idGroup) && !group.isPublic() ) {
            throw new AccessDeniedException("User " + currentUser.getEmail()
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
            throw new AccessDeniedException("You can't delete a group that you are not an admin of.");
        }

        groupRepository.deleteById(idGroup);
    }

    public List<GroupDTO> findByName(String name) {
        List<Group> groups = groupRepository.findAllByNameStartingWith(name);

        return groups.stream().map(groupMapper::entityToGroupDto).toList();
    }

    public ResolvedGroupRequestDTO createRequestToJoinGroup(Integer idGroup) throws BusinessLogicException, ResourceNotFoundException {
        User currentUser = jwtService.getUser();

        Group group = groupRepository.findById(idGroup).orElseThrow(() ->
                new BusinessLogicException(ERROR_CREATING_REQUEST_GROUP, "Group with id "
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

        return groupRequestMapper.requestToResolvedGroupRequestDTOStatusCreated(groupRequest);
    }


    public List<GroupRequestDTO> getAllRequestsForGroup(Integer idGroup) throws BusinessLogicException, ResourceNotFoundException {
        User currentUser = jwtService.getUser();
        Group group = groupRepository.findById(idGroup).orElseThrow(() ->
                new BusinessLogicException(ERROR_GETTING_GROUP_REQUESTS, "Group with id "
                        + idGroup + "does not exist"));

        if (!group.getAdmin().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not an admin of a given group " + group.getName());
        }
        List<GroupRequest> groupRequests = groupRequestRepository.findAllByGroup(group);

        return groupRequests.stream()
                .map(groupRequestMapper::requestToGroupRequestDTO)
                .toList();
    }

    public GroupRequest checkRequest(Integer idGroup, Integer idRequest) throws BusinessLogicException, ResourceNotFoundException {
        User currentUser = jwtService.getUser();

        Group group = groupRepository.findById(idGroup).orElseThrow(() ->
                new BusinessLogicException(ERROR_MANAGING_GROUP_REQUEST, "Group with id "
                        + idGroup + "does not exist"));

        GroupRequest request = groupRequestRepository.findById(idRequest).orElseThrow(() ->
                new BusinessLogicException(ERROR_MANAGING_GROUP_REQUEST, "Group request with id "
                        + idRequest + "does not exist"));

        User newMember = userRepository.findById(request.getUser().getId()).orElseThrow(() ->
                new BusinessLogicException(ERROR_MANAGING_GROUP_REQUEST, "User with id " + request.getUser().getId() + "does not exist"));

        if (!group.getAdmin().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not an admin of a given group " + group.getName());
        }

        if (!groupRequestRepository.existsByUserAndGroup(newMember, group)) {
            throw new BusinessLogicException(ERROR_MANAGING_GROUP_REQUEST, "Request for given user " + newMember +
                    " and group " + group + " does not exist");
        }

        if (group.isPublic()) {
            throw new BusinessLogicException(ERROR_MANAGING_GROUP_REQUEST, "You can't accept or reject a request if group is public");
        }

        return request;
    }

    public void acceptRequest(Integer idGroup, Integer idRequest) throws ResourceNotFoundException, BusinessLogicException {
        GroupRequest groupRequest = checkRequest(idGroup, idRequest);

        groupMemberRepository.save(new GroupMember(null, groupRequest.getUser(), groupRequest.getGroup()));
        groupRequestRepository.deleteById(idRequest);
    }

    public void rejectRequest(Integer idGroup, Integer idRequest) throws ResourceNotFoundException, BusinessLogicException {
        GroupRequest groupRequest = checkRequest(idGroup, idRequest);

        groupRequestRepository.deleteById(groupRequest.getId());
    }

    public ResolvedGroupRequestDTO addUserAsAMemberToPublicGroup(User user, Group group) {
        GroupMember groupMember = groupMemberRepository.save(new GroupMember(null, user, group));

        return groupRequestMapper.groupMemberToResolvedGroupRequestDTOStatusAccepted(groupMember);
    }

    public void leaveGroup(Integer idGroup) throws BusinessLogicException, ResourceNotFoundException {
        Group group = groupRepository.findById(idGroup).orElseThrow(() ->
                new BusinessLogicException(ERROR_LEAVING_GROUP, "Group with id "
                        + idGroup + "does not exist"));
        User user = jwtService.getUser();
        if (group.getAdmin().getId().equals(user.getId())) {
            throw new BusinessLogicException(ERROR_LEAVING_GROUP, "Admin can't leave a group.");
        }
        GroupMember groupMember = groupMemberRepository.findByMemberAndGroup(user, group).orElseThrow(() ->
                new BusinessLogicException(ERROR_LEAVING_GROUP, "You are not a member of that group!"));
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
            throw new BusinessLogicException(ERROR_REMOVING_MEMBER, "User with an id: " + idUser
                    + " is not a member of the group with id: " + idGroup);
        }
        groupMemberRepository.deleteGroupMemberByGroupIdAndMemberId(idGroup, idUser);
    }


}
