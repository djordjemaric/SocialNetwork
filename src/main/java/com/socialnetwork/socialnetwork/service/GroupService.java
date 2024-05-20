package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.GroupRequestRepository;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.socialnetwork.socialnetwork.repository.GroupRepository;

import java.util.List;
import java.util.Optional;


@Service
public class GroupService {
    private GroupRepository groupRepository;
    private GroupRequestRepository groupRequestRepository;
    private GroupMemberRepository groupMemberRepository;
    private GroupMapper groupMapper;


    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper,GroupMemberRepository groupMemberRepository,GroupRequestRepository groupRequestRepository) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.groupRequestRepository = groupRequestRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    public Group save(Integer idUser, CreateGroupDto group) {
        return groupRepository.save(groupMapper.createDtoToEntity(idUser,group));
    }

    public GroupRequest createRequestToJoinGroup(Integer idUser, Integer idGroup) {
        Optional<Group> group = groupRepository.findById(idGroup);
        GroupRequest groupRequest = groupMapper.createGroupRequestEntity(idUser,group.orElse(null));

        if(groupRequest.getGroup().isPublic()){

            // automatski pusti usr u grupu
            addUserAsAMemberToGroup(idUser,group.orElse(null));
        }

        return groupRequestRepository.save(groupRequest);
    }


    public GroupMember addUserAsAMemberToGroup(Integer idUser, Group group) {
        return groupMemberRepository.save(groupMapper.createGroupMemberEntity(idUser,group));
    }




    public List<Group> findByName(String name) {
        return  groupRepository.findAllByNameStartingWith(name);
    }


    public List<GroupRequest> getAllRequestsForGroup(Integer idGroup) {

        return  groupRepository.findById(idGroup).get().getGroupRequests();
    }

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

        if (!groupRepository.existsById(idGroup)) {
            groupRepository.deleteById(idGroup);
            return true;
        }
        return false;


    }



}
