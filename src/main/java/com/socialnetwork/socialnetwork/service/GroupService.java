package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
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

import java.util.List;
import java.util.Optional;


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

    public Group createGroup(CreateGroupDto group) {
        User currentUser = jwtService.getUser();

        //provera da li user sa tim emailom postoji
        if(userRepository.existsByEmail(currentUser.getEmail())){
            throw new FunctionArgumentException("User with that email does not exists!");
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

    public GroupRequest createRequestToJoinGroup( Integer idGroup) {
        User currentUser = jwtService.getUser();

        //provera da li user sa tim emailom postoji
        if(userRepository.existsByEmail(currentUser.getEmail())){
            throw new FunctionArgumentException("User with that email does not exists!");
        }

        //provera da li postoji grupa sa tim imenom
        if(!groupRepository.existsById(idGroup)){
            throw new FunctionArgumentException("Group with that name does not exists");
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

    public List<Group> findByName(String name) {

        //provera da li postoji/e grupa/e koje pocinju sa tim imenom
        if(!groupRepository.existsAllByNameStartingWith(name)){
            throw new FunctionArgumentException("There are no groups with that name");
        }

        return  groupRepository.findAllByNameStartingWith(name);
    }


    public List<GroupRequest> getAllRequestsForGroup(Integer idGroup) {
        User currentUser = jwtService.getUser();
        //provera da li postoji user
        if(userRepository.existsByEmail(currentUser.getEmail())){
            throw new FunctionArgumentException("User with that email does not exists!");
        }

        //provera da li postoji grupa sa prosledjenim id-jem i id-jem admina
        if(!groupRepository.existsByIdAndAdminId(idGroup, currentUser.getId())){
            throw new FunctionArgumentException("There is not group with that id or id of admin");
        }

        Group group = groupRepository.findById(idGroup).orElseThrow(() ->
                new FunctionArgumentException("Group does not exist!")
        );

        List<GroupRequest> groupRequests = groupRequestRepository.findAllByGroup(group);

        return  groupRequests;
    }

    public GroupMember acceptRequest( Integer idUser,Integer idGroup) {

        User currentUser = jwtService.getUser();

        //provera da li postoji admin i user tj member
        if(userRepository.existsByEmail(currentUser.getEmail()) &&  userRepository.existsById(idUser)){
            throw new FunctionArgumentException("User with that email does not exists!");
        }


        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist!"));
        User userAdmin = userRepository.findById(currentUser.getId()).orElseThrow(() -> new FunctionArgumentException("User with that id does not exist!"));
        User newMember = userRepository.findById(idUser).orElseThrow(() -> new FunctionArgumentException("User with that id does not exist!"));

        //provera da li je prosledjeni user admin ustvari admin te grupe
        if (!group.getAdmin().equals(userAdmin)) {
            throw new FunctionArgumentException("User with that email does not exists!");
        }

        GroupRequest groupRequest = null;
        //provera da li postoji request sa prosledjenim novim memberom i grupom u koju zeli da udje
        if (groupRequestRepository.existsByUserAndGroup(newMember, group)) {
            groupRequest = groupRequestRepository.findByUserAndGroup(newMember, group);
        }

        //provera da li je grupa private i ako jeste dodajemo usera u grupu
        GroupMember groupMember = null;
        if (!group.isPublic()) {
            groupMember = groupMapper.createGroupMemberEntity(newMember, group);
            groupMemberRepository.save(groupMember);
            groupRequestRepository.delete(groupRequest);
        }

        return groupMember;
    }

    public void rejectRequest(Integer idUser,Integer idGroup) {
        User currentUser = jwtService.getUser();
        //provera da li postoji admin i user tj member
        if(userRepository.existsByEmail(currentUser.getEmail()) &&  userRepository.existsById(idUser)){
            throw new FunctionArgumentException("User with that email does not exists!");
        }


        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist!"));
        User userAdmin = userRepository.findById(currentUser.getId()).orElseThrow(() -> new FunctionArgumentException("User with that id does not exist!"));
        User newMember = userRepository.findById(idUser).orElseThrow(() -> new FunctionArgumentException("User with that id does not exist!"));

        //provera da li je prosledjeni user admin ustvari admin te grupe
        if(!group.getAdmin().equals(userAdmin)){
            throw new FunctionArgumentException("User with that email does not exists!");
        }


        //provera da li postoji request sa prosledjenim userom koji ga zahteva i za prosledjenu grupu u koju zeli da udje
        if(groupRequestRepository.existsByUserAndGroup(newMember,group)){
            groupRequestRepository.delete(groupRequestRepository.findByUserAndGroup(newMember,group));
        }

    }

    public boolean deleteGroup(Integer idUser, Integer idGroup) {
//        //provera da li postoji user
//        if(userRepository.existsByEmail()){
//            throw new FunctionArgumentException("User with that email does not exists!");
//        }

        //provera da li postoji grupa sa prosldjenim id-jem
        if (groupRepository.existsById(idGroup)) {
            groupRepository.deleteById(idGroup);
            return true;
        }
        return false;
    }



}
