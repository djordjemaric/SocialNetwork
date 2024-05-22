package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.friendRequest.ResolvedFriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.FriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.PreviewFriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.SentFriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    public List<PreviewUserDTO> searchFriends(@RequestParam("searchTerm") String searchTerm){
        return friendsService.searchFriends(searchTerm);
    }

//    for now we will get id of the user that sent the request from path, but later will change it to it from JWT
    @ResponseStatus(HttpStatus.CREATED)
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

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteFriend(@PathVariable Integer id){
        friendsService.deleteFriend(id);
    }
}
