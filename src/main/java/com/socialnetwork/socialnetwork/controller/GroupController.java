package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupRequestDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestDTO;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.exceptions.BusinessLogicException;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public GroupDTO createGroup(@RequestBody CreateGroupDTO createGroupDto) throws BusinessLogicException, ResourceNotFoundException {
        return groupService.createGroup(createGroupDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable Integer id) throws ResourceNotFoundException {
        groupService.deleteGroup(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<GroupDTO> getGroupsByName(@RequestParam String name) {
        return groupService.findByName(name);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/requests")
    public List<GroupRequestDTO> getAllRequestForGroup(@PathVariable Integer id) throws ResourceNotFoundException, BusinessLogicException {
        return groupService.getAllRequestsForGroup(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{idGroup}/requests/{idRequest}/accept")
    public void acceptRequest(@PathVariable Integer idGroup, @PathVariable Integer idRequest) throws ResourceNotFoundException, BusinessLogicException {
        groupService.acceptRequest(idGroup, idRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{idGroup}/requests/{idRequest}/reject")
    public void rejectRequest(@PathVariable Integer idGroup, @PathVariable Integer idRequest) throws ResourceNotFoundException, BusinessLogicException {
        groupService.rejectRequest(idGroup, idRequest);
    }

    @DeleteMapping("/{idGroup}/leave")
    @ResponseStatus(HttpStatus.OK)
    public void leaveGroup(@PathVariable Integer idGroup) throws BusinessLogicException, ResourceNotFoundException {
        groupService.leaveGroup(idGroup);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{idGroup}/members/{idUser}")
    public void removeMember(@PathVariable Integer idGroup, @PathVariable Integer idUser) throws ResourceNotFoundException, BusinessLogicException {
        groupService.removeMember(idGroup, idUser);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{id}/join")
    public ResolvedGroupRequestDTO createRequestToJoinGroup(@PathVariable Integer id) throws BusinessLogicException, ResourceNotFoundException {
        return groupService.createRequestToJoinGroup(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/posts")
    public List<PostDTO> getAllPostsByGroupId(@PathVariable Integer id) throws ResourceNotFoundException, BusinessLogicException {
        return groupService.getAllPostsByGroupId(id);
    }

}
