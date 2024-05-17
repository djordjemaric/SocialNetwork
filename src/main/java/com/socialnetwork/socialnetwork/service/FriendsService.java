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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendsService {

    private FriendsRepository friendsRepository;
    private FriendRequestRepository friendRequestRepository;
    private UserRepository userRepository;
    private FriendRequestMapper friendRequestMapper;

    public FriendsService(FriendsRepository friendsRepository, FriendRequestRepository friendRequestRepository, UserRepository userRepository, FriendRequestMapper friendRequestMapper) {
        this.friendsRepository = friendsRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
        this.friendRequestMapper = friendRequestMapper;
    }

//    add 404 exception
    public PreviewFriendRequestDTO createFriendRequest(Integer senderId, SentFriendRequestDTO requestDTO){
        User friend = this.userRepository.findByEmail(requestDTO.friendsEmail()).orElse(null);
        if(friend == null){
            throw new RuntimeException("User not found");
        }
//        this should always differ from null, because we get it from JWT payload
        User currentUser = this.userRepository.findById(senderId).orElse(null);

//        checking if they are alreaady friends
        if(this.friendsRepository.areTwoUsersFriends(currentUser.getId(),friend.getId()).isEmpty()){

        }
//        check if there is a existing request between these two
        List<FriendRequest> userRequests = this.friendRequestRepository.getRequestsFromUser(currentUser.getId());
        if(!userRequests.stream().filter(req -> req.getFrom().getId().equals(friend.getId())).toList().isEmpty() ||
                !userRequests.stream().filter(req -> req.getTo().getId().equals(friend.getId())).toList().isEmpty()
        ){
            throw new RuntimeException("There is already a pending request between these users");
        }

//      Create and return a request
        return this.friendRequestMapper.entityToPreviewDTO(this.friendRequestRepository.save(this.friendRequestMapper.friendRequestFromUsers(currentUser, friend)));
    }

}
