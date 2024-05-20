package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.GroupRequestRepository;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.stereotype.Service;
import com.socialnetwork.socialnetwork.repository.GroupRepository;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupRequestRepository groupRequestRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMapper groupMapper;

    private final UserRepository userRepository;

    private final JwtService jwtService;
    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper, GroupMemberRepository groupMemberRepository, GroupRequestRepository groupRequestRepository, UserRepository userRepository,JwtService jwtService) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.groupRequestRepository = groupRequestRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;

    }



    public GroupRequest createRequestToJoinGroup( Integer idGroup) {
        User currentUser = jwtService.getUser();

        //provera da li user sa tim emailom postoji
        if(!userRepository.existsByEmail(currentUser.getEmail())){
            throw new FunctionArgumentException("User with that email does not exists!");
        }

        //provera da li postoji grupa sa tim imenom
        if(!groupRepository.existsById(idGroup)){
            throw new FunctionArgumentException("Group with that id does not exists");
        }

        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist!"));

        //provera da li je user vec u toj grupi
        if(groupMemberRepository.existsByUserIdAndGroupId(currentUser.getId(), idGroup)){
            throw new FunctionArgumentException("User is already in that group");
        }

        GroupRequest groupRequest = groupMapper.createGroupRequestEntity(currentUser, group);
        groupRequest = groupRequestRepository.save(groupRequest);

        return groupRequest;
    }


    public void addUserAsAMemberToPublicGroup(GroupRequest groupRequest) {

        User newMember = userRepository.findById(groupRequest.getUser().getId()).orElseThrow(() -> new FunctionArgumentException("User does not exist!"));
        Integer idGroup = groupRequest.getGroup().getId();

        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist!"));

        //ako je grupa public automatski dozvoljavamo korisniku pristup i brisemo postojeci request
        if (group.isPublic()) {
            GroupMember groupMember = groupMapper.createGroupMemberEntity(newMember,group);
            groupMemberRepository.save(groupMember);
            groupRequestRepository.delete(groupRequest);
        }

    }

}
