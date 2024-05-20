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
import java.util.Objects;
import java.util.Optional;


@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupRequestRepository groupRequestRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMapper groupMapper;

    private final UserRepository userRepository;


    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper, GroupMemberRepository groupMemberRepository, GroupRequestRepository groupRequestRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.groupRequestRepository = groupRequestRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
    }

    public Group createGroup(Integer idUser, CreateGroupDto group) {

//        //provera da li user sa tim emailom postoji
//        if(userRepository.existsByEmail()){
//            throw new FunctionArgumentException("User with that email does not exists!");
//        }

        //provera da li postoji grupa sa tim imenom
        if(groupRepository.existsByName(group.name())){
            throw new FunctionArgumentException("Group with that name already exists");

        }

        //kreiranje grupe
        Group createdGroup = groupRepository.save(groupMapper.createDtoToEntity(idUser,group));

        //kreiranje membera za tu grupu
        groupMemberRepository.save(groupMapper.createGroupMemberEntity(idUser,createdGroup));

        return createdGroup;
    }

    public GroupRequest createRequestToJoinGroup(Integer idUser, Integer idGroup) {

//        //provera da li user sa tim emailom postoji
//        if(userRepository.existsByEmail()){
//            throw new FunctionArgumentException("User with that email does not exists!");
//        }

        //provera da li postoji grupa sa tim imenom
        if(!groupRepository.existsById(idGroup)){
            throw new FunctionArgumentException("Group with that name does not exists");
        }

        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist!"));

        //provera da li je user vec u toj grupi
        if(groupMemberRepository.existsByUserIdAndGroupId(idUser,idGroup)){
            throw new FunctionArgumentException("User is already in that group");
        }

        GroupRequest groupRequest = groupMapper.createGroupRequestEntity(idUser,group);
        groupRequest = groupRequestRepository.save(groupRequest);

        return groupRequest;
    }


    public void addUserAsAMemberToPublicGroup(GroupRequest groupRequest) {
        Integer idUser = groupRequest.getUser().getId();
        Integer idGroup = groupRequest.getGroup().getId();

        Group group = groupRepository.findById(idGroup).orElseThrow(() -> new FunctionArgumentException("Group does not exist!"));
        if (group.isPublic()) {
            GroupMember groupMember = groupMapper.createGroupMemberEntity(idUser,group);
            groupMemberRepository.save(groupMember);
            groupRequestRepository.delete(groupRequest);
        }

    }




    public List<Group> findByName(String name) {

        if(!groupRepository.existsAllByNameStartingWith(name)){
            throw new FunctionArgumentException("There are no groups with that name");
        }

        return  groupRepository.findAllByNameStartingWith(name);
    }


//    public List<GroupRequest> getAllRequestsForGroup(Integer idGroup) {
//
//        return  groupRepository.findById(idGroup).get().getGroupRequests();
//    }

    public GroupMember acceptRequest(Integer IdUser , Integer idGroup) {

        if (groupRepository.existsById(idGroup)) {
            Optional<GroupRequest> request = groupRequestRepository.findById(idGroup);
            if (request.isPresent()) {

                Optional<Group> group = groupRepository.findById(idGroup);
                GroupMember groupMember = groupMapper.createGroupMemberEntity(request.get().getUser().getId(),group.orElseGet(null));

                groupRequestRepository.deleteById(idGroup);

                return groupMemberRepository.save(groupMember);


            }
        }
        return null;

    }

    public boolean rejectRequest(Integer idUser, Integer idGroup) {


        if (!groupRepository.existsById(idGroup)) {
            return false; // Group not found
        }

        // Check if the request exists
        Optional<GroupRequest> request = groupRequestRepository.findById(idGroup);
        if (request.isPresent()) {
            groupRequestRepository.deleteById(idGroup);
            return true; // Successfully deleted the request
        } else {
            return false; // Request not found
        }

    }

    public boolean deleteGroup(Integer idUser, Integer idGroup) {

        if (groupRepository.existsById(idGroup)) {
            groupRepository.deleteById(idGroup);
            return true;
        }
        return false;
    }


    public void removeMember (Integer idAdmin, Integer idGroup, Integer idUser){

        // need to check if the group with that admin and group id exists
        // need to check if the user is in the group and if he is the admin
        // need to remove user from GroupMember table
        if (groupRepository.existsByAdminIdAndGroupId(idAdmin, idGroup)) {
            if (groupMemberRepository.existsByUserIdAndGroupId(idUser, idGroup)) {
                if (!Objects.equals(idAdmin, idUser)){
                    groupMemberRepository.deleteGroupMemberByGroupIdAndMemberId(idGroup, idUser);

                } else {
                    throw new FunctionArgumentException("Can't remove an admin from the group!");
                }

            } else {
                throw new FunctionArgumentException("User with that is not in this group");
            }

        } else {
            throw new FunctionArgumentException("There are no groups with that tuple (IdAdmin, IdGroup)");
        }
    }

}
