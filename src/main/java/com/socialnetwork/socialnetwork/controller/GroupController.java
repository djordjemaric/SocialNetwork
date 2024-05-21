package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.group.GroupMemberDto;
import com.socialnetwork.socialnetwork.dto.group.GroupRequestDto;
import org.springframework.http.HttpStatus;
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping( "/requests/{idGroup}")
    public List<GroupRequestDto> getAllRequestForGroup(@PathVariable Integer idGroup) {
        return groupService.getAllRequestsForGroup(idGroup);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping( "/accept/{idUser}/{idGroup}")
    public GroupMemberDto acceptRequest(@PathVariable Integer idUser, @PathVariable Integer idGroup) {
        return groupService.acceptRequest(idUser,idGroup);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/reject/{idUser}/{idGroup}")
    public void rejectRequest(@PathVariable Integer idUser,@PathVariable Integer idGroup) {
        groupService.rejectRequest(idUser,idGroup);
    }

}
