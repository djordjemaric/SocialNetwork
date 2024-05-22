package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.service.GroupService;
import com.socialnetwork.socialnetwork.dto.group.GroupRequest_MemberDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;


    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public GroupDTO createGroup(@RequestBody CreateGroupDTO createGroupDto) {
        return groupService.createGroup(createGroupDto);
    }

    @DeleteMapping("/{idGroup}/leave")
    @ResponseStatus(HttpStatus.OK)
    public void leaveGroup(@PathVariable Integer idGroup) {
        groupService.leaveGroup(idGroup);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping( "/{idGroup}/members/{idUser}") // idGroup and idUser that we want to remove
    public void removeMember (@PathVariable Integer idGroup, @PathVariable Integer idUser)
    {
        groupService.removeMember(idGroup, idUser);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping( "/{id}/join")
    public GroupRequest_MemberDTO createRequestToJoinGroup(@PathVariable Integer id) {

        return groupService.createRequestToJoinGroup(id);
    }

}
