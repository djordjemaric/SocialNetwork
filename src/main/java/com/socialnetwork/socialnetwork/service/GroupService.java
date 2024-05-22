package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.GroupDto;
import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
import com.socialnetwork.socialnetwork.dto.post.PostDto;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.mapper.PostMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.PostRepository;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.stereotype.Service;
import com.socialnetwork.socialnetwork.repository.GroupRepository;

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


    public List<PostDto> getAllPostsByGroupId(Integer idGroup) {
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
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            postDtos.add(postMapper.entityToPostDto(post));
        }

        return postDtos;

    }

}
