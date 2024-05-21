package com.socialnetwork.socialnetwork.controller;

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


    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{idGroup}")
    public void deleteGroup(@PathVariable Integer idGroup) {
        groupService.deleteGroup(idGroup);
    }

}
