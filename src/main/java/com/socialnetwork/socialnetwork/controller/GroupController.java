package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.group.GroupDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.socialnetwork.socialnetwork.service.GroupService;

import java.util.List;


@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/find")
    public List<GroupDto> getGroupsByName(@RequestParam String name) {
        return groupService.findByName(name);
    }


}
