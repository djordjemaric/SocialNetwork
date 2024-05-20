package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.socialnetwork.socialnetwork.service.GroupService;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping( "/{idUser}")
    public Group createGroup(@PathVariable Integer idUser, @RequestBody CreateGroupDto createGroupDto) {
        return groupService.createGroup(idUser, createGroupDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping( "")
    public List<Group> getGroupsByName(@RequestParam String name) {
        return groupService.findByName(name);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping( "/{idUser}/{idGroup}")
    public void createRequestToJoinGroup(@PathVariable Integer idUser, @PathVariable Integer idGroup) {

        GroupRequest groupRequest = groupService.createRequestToJoinGroup(idUser, idGroup);
        groupService.addUserAsAMemberToPublicGroup(groupRequest);

    }


//    @GetMapping( "/{idGroup}")
//    public List<Req> getAllRequestForGroup(@PathVariable Integer idGroup) {
//
//        return ResponseEntity.ok(groupService.getAllRequestsForGroup(idGroup));
//    }

    @PostMapping( "/accept/{idUser}/{IdGroup}")
    public ResponseEntity acceptRequest(@PathVariable Integer idUser,@PathVariable Integer idGroup) {

        return ResponseEntity.ok(groupService.acceptRequest(idUser, idGroup));
    }

    @PostMapping("/reject/{idUser}/{idGroup}")
    public ResponseEntity<String> rejectRequest(@PathVariable Integer idUser, @PathVariable Integer idGroup) {
        try {
            boolean success = groupService.rejectRequest(idUser, idGroup);
            if (success) {
                return ResponseEntity.ok("Request rejected successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Group not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while rejecting the request.");
        }
    }

    @DeleteMapping("/delete/{idUser}/{idGroup}")
    public ResponseEntity<String> deleteGroup(@PathVariable Integer idUser, @PathVariable Integer idGroup) {
        try {
            boolean success = groupService.deleteGroup(idUser, idGroup);
            if (success) {
                return ResponseEntity.ok("Group deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Group not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while rejecting the request.");
        }
    }


    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    @DeleteMapping( "/{idAdmin}/{idGroup}/{idUser}") // idAdmin, idGroup and idUser that we want to remove
    public void removeMember (@PathVariable Integer idAdmin, @PathVariable Integer idGroup, @PathVariable Integer idUser)
    {
        groupService.removeMember(idAdmin, idGroup, idUser);
    }





}
