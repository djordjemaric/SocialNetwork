package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.friendRequest.AcceptRequestDTO;
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
    @GetMapping("")
    public Object getFriendRequests(@RequestParam("userId") Integer userId){
        return null;
    }

    @GetMapping("/search")
    public List<Object> searchFriends(@RequestParam("searchTerm") String searchTerm){
        return null;
    }

//    for now we will get id of the user that sent the request from path, but later will change it to it from JWT
    @PostMapping("")
    public Object sendFriendRequest(@RequestParam("userId") Integer userId, @RequestBody SentFriendRequestDTO friendRequestDTO){
        return null;
    }

    @PostMapping("/accept-friend/{friendRequestId}")
    public Object acceptFriendRequest(@PathVariable Integer friendRequestId, @RequestBody AcceptRequestDTO acceptRequestDTO){
        return null;
    }

    @DeleteMapping("/{friendId}")
    public void deleteFriend(@PathVariable Integer friendId){
    }
}
