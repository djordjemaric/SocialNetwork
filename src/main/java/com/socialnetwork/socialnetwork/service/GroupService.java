package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.CreateGroupDto;
import com.socialnetwork.socialnetwork.dto.GroupDto;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.stereotype.Service;
import com.socialnetwork.socialnetwork.repository.GroupRepository;


@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMapper groupMapper;
    private final JwtService jwtService;

    public GroupService(GroupRepository groupRepository, JwtService jwtService, GroupMemberRepository groupMemberRepository, GroupMapper groupMapper) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
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

    public void deleteGroup(Integer idGroup) {

        User currentUser = jwtService.getUser();

        //provera da li postoji grupa sa prosledjenim id-jem i id-jem admina
        if (!groupRepository.existsByIdAndAdminId(idGroup, currentUser.getId())) {
            throw new FunctionArgumentException("There is no group with given id or id of admin");
        }

        groupRepository.deleteById(idGroup);

    }


}
