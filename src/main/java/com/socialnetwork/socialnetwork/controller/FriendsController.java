package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.friendRequest.ResolvedFriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.FriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.PreviewFriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.SentFriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.exceptions.BusinessLogicException;
import com.socialnetwork.socialnetwork.exceptions.ErrorCode;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.service.FriendsService;
import jakarta.validation.Valid;
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/requests")
    public List<FriendRequestDTO> getFriendRequests() throws ResourceNotFoundException {
        return friendsService.getAllPendingRequestsForUser();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    public List<PreviewUserDTO> searchFriends(@RequestParam("searchTerm") String searchTerm) throws ResourceNotFoundException {
        return friendsService.searchFriends(searchTerm);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/requests")
    public PreviewFriendRequestDTO sendFriendRequest(@RequestBody @Valid SentFriendRequestDTO friendRequestDTO) throws ResourceNotFoundException, BusinessLogicException {
        return friendsService.createFriendRequest(friendRequestDTO);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/requests/{friendRequestId}/accept")
    public ResolvedFriendRequestDTO acceptFriendRequest(@PathVariable Integer friendRequestId) throws ResourceNotFoundException {
        return friendsService.acceptRequest(friendRequestId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/requests/{friendRequestId}/decline")
    public ResolvedFriendRequestDTO declineFriendRequest(@PathVariable Integer friendRequestId) throws ResourceNotFoundException {
        return friendsService.declineRequest(friendRequestId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteFriend(@PathVariable Integer id) throws ResourceNotFoundException, BusinessLogicException {
        friendsService.deleteFriend(id);
    }
}
