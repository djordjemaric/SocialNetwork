package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.socialnetwork.socialnetwork.service.GroupService;

@CrossOrigin
@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }



    @PostMapping( "/{idUser}")
    public ResponseEntity createGroup(@PathVariable Integer idUser, @RequestBody CreateGroupDto createGroupDto) {
        System.out.println(createGroupDto.isPublic());
        return ResponseEntity.ok(groupService.save(idUser, createGroupDto));
    }


    @PostMapping( "/{idUser}/{idGroup}")
    public ResponseEntity createRequestToJoinGroup(@PathVariable Integer idUser,@PathVariable Integer idGroup) {

        return ResponseEntity.ok(groupService.createRequestToJoinGroup(idUser, idGroup));
    }


    @GetMapping( "/{idGroup}")
    public ResponseEntity getAllRequestForGroup(@PathVariable Integer idGroup) {

        return ResponseEntity.ok(groupService.getAllRequestsForGroup(idGroup));
    }

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


    @GetMapping( "")
    public ResponseEntity getGroupsByName(@RequestParam String name) {
        // Your logic to get all groups based on the groupId
        return ResponseEntity.ok(groupService.findByName(name));
    }



}
