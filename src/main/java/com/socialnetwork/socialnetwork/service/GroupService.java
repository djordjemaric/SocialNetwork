package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
import com.socialnetwork.socialnetwork.dto.group.GroupDto;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper, GroupMemberRepository groupMemberRepository, UserRepository userRepository, JwtService jwtService) {
        this.groupRepository = groupRepository;
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

    public List<GroupDto> findByName(String name) {

        //provera da li postoji/e grupa/e koje pocinju sa tim imenom
        if (!groupRepository.existsAllByNameStartingWith(name)) {
            throw new FunctionArgumentException("There are no groups with that name");
        }

        List<Group> groups = groupRepository.findAllByNameStartingWith(name);
        List<GroupDto> groupDtos = new ArrayList<>();

        for (Group gr : groups) {
            groupDtos.add(groupMapper.entityToGroupDto(gr));
        }

        return groupDtos;
    }


}
