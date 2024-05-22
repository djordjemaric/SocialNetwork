package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupMemberDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupRequestDTO;
import com.socialnetwork.socialnetwork.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping
    public GroupDTO createGroup(@RequestBody CreateGroupDTO createGroupDto) {
        return groupService.createGroup(createGroupDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping( "/{idGroup}/requests")
    public List<GroupRequestDTO> getAllRequestForGroup(@PathVariable Integer idGroup) {
        System.out.println(idGroup);
        return groupService.getAllRequestsForGroup(idGroup);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping( "/{idUser}/{idGroup}/accept")
    public GroupMemberDTO acceptRequest(@PathVariable Integer idUser, @PathVariable Integer idGroup) {
        return groupService.acceptRequest(idUser,idGroup);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{idUser}/{idGroup}/reject")
    public void rejectRequest(@PathVariable Integer idUser,@PathVariable Integer idGroup) {
        groupService.rejectRequest(idUser,idGroup);
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

}
