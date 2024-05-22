package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.CreateGroupDto;
import com.socialnetwork.socialnetwork.dto.GroupDto;
import com.socialnetwork.socialnetwork.dto.GroupRequest_MemberDto;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.socialnetwork.socialnetwork.service.GroupService;


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


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping( "/{id}/join")
    public GroupRequest_MemberDto createRequestToJoinGroup(@PathVariable Integer id) {

        return groupService.createRequestToJoinGroup(id);
    }

}
