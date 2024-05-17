package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.friendRequest.AcceptRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.PreviewFriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.SentFriendRequestDTO;
import com.socialnetwork.socialnetwork.service.FriendsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendsController {

    private FriendsService friendsService;

    public FriendsController(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

//    Logger logger = LoggerFactory.getLogger(FriendsController.class);

//    for now we will get id of the user that sent the request from path, but later will change it to it from JWT
    @GetMapping("/requests")
    public Object getFriendRequests(@RequestParam("userId") Integer userId){
        return null;
    }

    @GetMapping("/search")
    public List<Object> searchFriends(@RequestParam("searchTerm") String searchTerm){
        return null;
    }

//    for now we will get id of the user that sent the request from path, but later will change it to it from JWT
    @PostMapping("/requests")
    public PreviewFriendRequestDTO sendFriendRequest(@RequestParam("userId") Integer userId, @RequestBody SentFriendRequestDTO friendRequestDTO){
        return this.friendsService.createFriendRequest(userId,friendRequestDTO);
    }

    @PostMapping("/requests/{friendRequestId}/accept")
    public Object acceptFriendRequest(@PathVariable Integer friendRequestId){
        return null;
    }

    @PostMapping("/requests/{friendRequestId}/decline")
    public Object declineFriendRequest(@PathVariable Integer friendRequestId){
        return null;
    }

    @DeleteMapping("/{friendId}")
    public void deleteFriend(@PathVariable Integer friendId){
    }
}
