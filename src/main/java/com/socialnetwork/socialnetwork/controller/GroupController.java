package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.GroupDto;
import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.socialnetwork.socialnetwork.service.GroupService;


@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;
    private final JwtService jwtService;

    public GroupController(GroupService groupService, JwtService jwtService) {
        this.groupService = groupService;
        this.jwtService = jwtService;
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public GroupDto createGroup(@RequestBody CreateGroupDto createGroupDto) {
        return groupService.createGroup(createGroupDto);
    }

    @DeleteMapping("/leave/{idGroup}")
    public ResponseEntity<String> leaveGroup(@PathVariable Integer idGroup) {
        String userSub = jwtService.getUserSub();
        groupService.leaveGroup(userSub, idGroup);
        return ResponseEntity.ok("User left group successfully.");
    }
}
