package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.group.GroupDto;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.stereotype.Service;
import com.socialnetwork.socialnetwork.repository.GroupRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    public GroupService(GroupRepository groupRepository,GroupMapper groupMapper) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
    }

    public List<GroupDto> findByName(String name) {

        //provera da li postoji/e grupa/e koje pocinju sa tim imenom
        if (!groupRepository.existsAllByNameStartingWith(name)) {
            throw new FunctionArgumentException("There are no groups with that name");
        }

        List<Group> groups = groupRepository.findAllByNameStartingWith(name);
        List<GroupDto> groupDtos = new ArrayList<>();

        for(Group gr: groups){
            groupDtos.add(groupMapper.entityToGroupDto(gr));
        }

        return groupDtos;
    }


}
