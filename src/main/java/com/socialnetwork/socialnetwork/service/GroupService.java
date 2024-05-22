package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.GroupDto;
import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.mapper.PostMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.PostRepository;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;


import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMapper groupMapper;
    private final PostMapper postMapper;

    private final PostRepository postRepository;

    private final JwtService jwtService;

    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper, GroupMemberRepository groupMemberRepository, JwtService jwtService, PostRepository postRepository, PostMapper postMapper) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.groupMemberRepository = groupMemberRepository;
        this.postRepository = postRepository;
        this.postMapper = postMapper;
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


    public List<PostDTO> getAllPostsByGroupId(Integer idGroup) {
        User currentUser = jwtService.getUser();

        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group with that id does not exists"));
        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroup(group);
        List<User> members = new ArrayList<>();

        for (GroupMember groupMember : groupMembers) {
            members.add(groupMember.getMember());
        }

        //provera da li je user u toj grupi iz koje zahteva da vidi postove
        if (!members.contains(currentUser)) {
            throw new FunctionArgumentException("User is not member of give group!");
        }

        List<Post> posts = postRepository.findAllByGroup_Id(idGroup);
        List<PostDTO> postDTOS = new ArrayList<>();
        for (Post post : posts) {
            postDTOS.add(postMapper.postToPostDTO(post));
        }

        return postDTOS;

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
     public void removeMember (Integer idGroup, Integer idUser){

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
