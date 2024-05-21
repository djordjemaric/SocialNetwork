package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.entity.User;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.stereotype.Service;
import com.socialnetwork.socialnetwork.repository.GroupRepository;


@Service
public class GroupService {
    private final GroupRepository groupRepository;

    private final JwtService jwtService;

    public GroupService(GroupRepository groupRepository, JwtService jwtService) {
        this.groupRepository = groupRepository;
        this.jwtService = jwtService;
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
