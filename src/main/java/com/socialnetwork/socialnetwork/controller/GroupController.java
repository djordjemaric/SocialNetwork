package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.GroupDto;
import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
import com.socialnetwork.socialnetwork.service.GroupService;
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
    public GroupDto createGroup(@RequestBody CreateGroupDto createGroupDto) {
        return groupService.createGroup(createGroupDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping( "/{idGroup}/members/{idUser}") // idGroup and idUser that we want to remove
    public void removeMember (@PathVariable Integer idGroup, @PathVariable Integer idUser)
    {
        groupService.removeMember(idGroup, idUser);
    }





}
