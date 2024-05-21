package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.friendRequest.PreviewFriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.SentFriendRequestDTO;
import com.socialnetwork.socialnetwork.entity.FriendRequest;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.FriendRequestMapper;
import com.socialnetwork.socialnetwork.repository.FriendRequestRepository;
import com.socialnetwork.socialnetwork.repository.FriendsRepository;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FriendsService {

    private final FriendsRepository friendsRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final FriendRequestMapper friendRequestMapper;
    private final JwtService jwtService;

    public FriendsService(FriendsRepository friendsRepository, FriendRequestRepository friendRequestRepository, UserRepository userRepository, FriendRequestMapper friendRequestMapper, JwtService jwtService) {
        this.friendsRepository = friendsRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
        this.friendRequestMapper = friendRequestMapper;
        this.jwtService = jwtService;
    }

//    add 404 exception
    public PreviewFriendRequestDTO createFriendRequest(SentFriendRequestDTO requestDTO){
        User friend = userRepository.findByEmail(requestDTO.friendsEmail()).orElseThrow(() -> new RuntimeException("User not found"));

//        extracting user from JwtService
        User currentUser = jwtService.getUser();

//        checking if they are alreaady friends
        if(friendsRepository.areTwoUsersFriends(currentUser.getId(),friend.getId()).isPresent()){
            throw new RuntimeException("These users are already friends");
        }
//        check if there is a existing request between these two
        if(friendRequestRepository.doesRequestExistsBetweenUsers(currentUser.getId(), friend.getId()).isPresent()){
            throw new RuntimeException("There is already a pending request between these users");
        }

//      Create and return a request
        FriendRequest savedFriendRequest = friendRequestRepository.save(friendRequestMapper.friendRequestFromUsers(currentUser, friend));
        return friendRequestMapper.entityToPreviewDTO(savedFriendRequest);
    }

    public List<User> searchFriends(String searchTerm){
        return null;
    }
}
