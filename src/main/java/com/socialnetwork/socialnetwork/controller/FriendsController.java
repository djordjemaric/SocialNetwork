package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.friendRequest.PreviewFriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.SentFriendRequestDTO;
import com.socialnetwork.socialnetwork.service.FriendsService;
import com.socialnetwork.socialnetwork.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendsController {

    private final FriendsService friendsService;
    private final JwtService jwtService;

    public FriendsController(FriendsService friendsService, JwtService jwtService) {
        this.friendsService = friendsService;
        this.jwtService = jwtService;
    }

//    Logger logger = LoggerFactory.getLogger(FriendsController.class);

//    for now we will get id of the user that sent the request from path, but later will change it to it from JWT
    @GetMapping("/requests")
    public Object getFriendRequests(){
        System.out.println(jwtService.getUser());
        return null;
    }

    @GetMapping("/search")
    public List<Object> searchFriends(@RequestParam("searchTerm") String searchTerm){
        return null;
    }

//    for now we will get id of the user that sent the request from path, but later will change it to it from JWT
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/requests")
    public PreviewFriendRequestDTO sendFriendRequest(@RequestBody SentFriendRequestDTO friendRequestDTO){
        return friendsService.createFriendRequest(friendRequestDTO);
    }

    @PostMapping("/requests/{friendRequestId}/accept")
    public Object acceptFriendRequest(@PathVariable Integer friendRequestId){
        return null;
    }

    @PostMapping("/requests/{friendRequestId}/decline")
    public Object declineFriendRequest(@PathVariable Integer friendRequestId){
        return null;
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteFriend(@PathVariable Integer id){
        friendsService.deleteFriend(id);
    }
}
