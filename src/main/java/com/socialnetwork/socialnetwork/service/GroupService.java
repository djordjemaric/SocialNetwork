package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.stereotype.Service;
import com.socialnetwork.socialnetwork.repository.GroupRepository;

import java.util.List;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMapper groupMapper;

    private final UserRepository userRepository;

    private final JwtService jwtService;
    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper, GroupMemberRepository groupMemberRepository,UserRepository userRepository,JwtService jwtService) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;

    }

    public Group createGroup(CreateGroupDto group) {
        User currentUser = jwtService.getUser();

        //provera da li user sa tim emailom postoji
        if(!userRepository.existsByEmail(currentUser.getEmail())){
            System.out.println(currentUser.getEmail());
            throw new FunctionArgumentException("User with that email does not exists!!");
        }

        //provera da li postoji grupa sa tim imenom
        if(groupRepository.existsByName(group.name())){
            throw new FunctionArgumentException("Group with that name already exists");

        }

        //kreiranje grupe
        Group createdGroup = groupRepository.save(groupMapper.createDtoToEntity(currentUser, group));

        //dodavanje admina kao membera u tu grupu
        groupMemberRepository.save(groupMapper.createGroupMemberEntity(currentUser, createdGroup));

        return createdGroup;
    }

    public List<Group> findByName(String name) {

        //provera da li postoji/e grupa/e koje pocinju sa tim imenom
        if(!groupRepository.existsAllByNameStartingWith(name)){
            throw new FunctionArgumentException("There are no groups with that name");
        }

        return  groupRepository.findAllByNameStartingWith(name);
    }


}
