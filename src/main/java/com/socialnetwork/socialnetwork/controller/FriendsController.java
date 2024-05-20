package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.friendRequest.ResolvedFriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.FriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.PreviewFriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.SentFriendRequestDTO;
import com.socialnetwork.socialnetwork.entity.Friends;
import com.socialnetwork.socialnetwork.service.FriendsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendsController {

    private final FriendsService friendsService;

    public FriendsController(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

//    Logger logger = LoggerFactory.getLogger(FriendsController.class);

//    for now we will get id of the user that sent the request from path, but later will change it to it from JWT
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/requests")
    public List<FriendRequestDTO> getFriendRequests(){
        return friendsService.getAllPendingRequestsForUser();
    }

    @GetMapping("/search")
    public List<Object> searchFriends(@RequestParam("searchTerm") String searchTerm){
        return null;
    }

//    for now we will get id of the user that sent the request from path, but later will change it to it from JWT
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/requests")
    public PreviewFriendRequestDTO sendFriendRequest(@RequestBody SentFriendRequestDTO friendRequestDTO){
        return friendsService.createFriendRequest(friendRequestDTO);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/requests/{friendRequestId}/accept")
    public ResolvedFriendRequestDTO acceptFriendRequest(@PathVariable Integer friendRequestId){
        return friendsService.acceptRequest(friendRequestId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/requests/{friendRequestId}/decline")
    public ResolvedFriendRequestDTO declineFriendRequest(@PathVariable Integer friendRequestId){
        return friendsService.declineRequest(friendRequestId);
    }

    @DeleteMapping("/{friendId}")
    public void deleteFriend(@PathVariable Integer friendId){
    }
}
