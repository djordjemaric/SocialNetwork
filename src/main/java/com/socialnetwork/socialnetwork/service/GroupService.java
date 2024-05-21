package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.group.GroupMemberDto;
import com.socialnetwork.socialnetwork.dto.group.GroupRequestDto;
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

import java.util.ArrayList;
import java.util.List;


@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupRequestRepository groupRequestRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMapper groupMapper;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper, GroupMemberRepository groupMemberRepository, GroupRequestRepository groupRequestRepository, UserRepository userRepository, JwtService jwtService) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.groupRequestRepository = groupRequestRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;

    }

    public List<GroupRequestDto> getAllRequestsForGroup(Integer idGroup) {
        User currentUser = jwtService.getUser();

        //provera da li postoji grupa sa prosledjenim id-jem i id-jem admina
        if (!groupRepository.existsByIdAndAdminId(idGroup, currentUser.getId())) {
            throw new FunctionArgumentException("There is not group with that id or id of admin");
        }

        Group group = groupRepository.findById(idGroup).orElseThrow(() ->
                new FunctionArgumentException("Group does not exist!")
        );

        List<GroupRequest> groupRequests = groupRequestRepository.findAllByGroup(group);
        List<GroupRequestDto> groupRequestDtos = new ArrayList<>();

        //mapiramo groupRequestove u groupReqeustDto-ove
        for (GroupRequest groupRequest : groupRequests) {
            groupRequestDtos.add(groupMapper.groupRequestToGroupRequestDto(groupRequest));
        }

        return groupRequestDtos;
    }

    public GroupMemberDto acceptRequest(Integer idUser, Integer idGroup) {

        User currentUser = jwtService.getUser();

        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist!"));
        User newMember = userRepository.findById(idUser).orElseThrow(() -> new FunctionArgumentException("User with that id does not exist!"));

        //provera da li je prosledjeni user admin ustvari admin te grupe
        if (!group.getAdmin().equals(currentUser)) {
            throw new FunctionArgumentException("That user is not an admin for that group!");
        }

        //provera da li postoji request sa prosledjenim novim memberom i grupom u koju zeli da udje
        if (!groupRequestRepository.existsByUserAndGroup(newMember, group)) {
            throw new FunctionArgumentException("That request does not exist");
        }

        //provera da li je grupa private i ako jeste dodajemo usera u grupu
        GroupMember groupMember = null;
        if (!group.isPublic()) {

            groupMember = groupMemberRepository.save(new GroupMember(null, newMember, group));
            groupRequestRepository.delete(groupRequestRepository.findByUserAndGroup(newMember, group));
        }

        return groupMapper.groupMemberToGroupMemberDto(groupMember);
    }

    public void rejectRequest(Integer idUser, Integer idGroup) {
        User currentUser = jwtService.getUser();

        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist!"));
        User newMember = userRepository.findById(idUser).orElseThrow(() -> new FunctionArgumentException("User with that id does not exist!"));

        //provera da li je prosledjeni user admin ustvari admin te grupe
        if (!group.getAdmin().equals(currentUser)) {
            throw new FunctionArgumentException("That user is not an admin for that group!");
        }


        //provera da li postoji request sa prosledjenim userom koji ga zahteva i za prosledjenu grupu u koju zeli da udje
        if (!groupRequestRepository.existsByUserAndGroup(newMember, group)) {
            throw new FunctionArgumentException("That request does not exist");
        }
        groupRequestRepository.delete(groupRequestRepository.findByUserAndGroup(newMember, group));

    }
}
