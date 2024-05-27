package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.friendRequest.ResolvedFriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.FriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.PreviewFriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.SentFriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.entity.FriendRequest;
import com.socialnetwork.socialnetwork.entity.Friends;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.BusinessLogicException;
import com.socialnetwork.socialnetwork.exceptions.ErrorCode;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.mapper.FriendRequestMapper;
import com.socialnetwork.socialnetwork.mapper.FriendsMapper;
import com.socialnetwork.socialnetwork.repository.FriendRequestRepository;
import com.socialnetwork.socialnetwork.repository.FriendsRepository;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FriendsService {

    private final FriendsRepository friendsRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final FriendRequestMapper friendRequestMapper;
    private final FriendsMapper friendsMapper;
    private final JwtService jwtService;

    public FriendsService(FriendsRepository friendsRepository, FriendRequestRepository friendRequestRepository, UserRepository userRepository, FriendRequestMapper friendRequestMapper, FriendsMapper friendsMapper, JwtService jwtService) {
        this.friendsRepository = friendsRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
        this.friendRequestMapper = friendRequestMapper;
        this.friendsMapper = friendsMapper;
        this.jwtService = jwtService;
    }

    public PreviewFriendRequestDTO createFriendRequest(SentFriendRequestDTO requestDTO) throws ResourceNotFoundException, BusinessLogicException {
        User friend = userRepository.findByEmail(requestDTO.friendsEmail())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERROR_FINDING_USER, "User not found. Wrong email sent"));

        User currentUser = jwtService.getUser();

        if(currentUser.getId().equals(friend.getId())){
            throw new BusinessLogicException(ErrorCode.ERROR_CREATING_FRIEND_REQUEST, "Can't send request to yourself");
        }

        if(friendsRepository.areTwoUsersFriends(currentUser.getId(),friend.getId()).isPresent()){
            throw new BusinessLogicException(ErrorCode.ERROR_CREATING_FRIEND_REQUEST, "These users are already friends");
        }
        if(friendRequestRepository.doesRequestExistsBetweenUsers(currentUser.getId(), friend.getId()).isPresent()){
            throw new BusinessLogicException(ErrorCode.ERROR_CREATING_FRIEND_REQUEST, "There is already a pending request between these users");
        }

        FriendRequest savedFriendRequest = friendRequestRepository.save(friendRequestMapper.friendRequestFromUsers(currentUser, friend));
        return friendRequestMapper.entityToPreviewDTO(savedFriendRequest);
    }

    public List<PreviewUserDTO> searchFriends(String searchTerm) throws ResourceNotFoundException {
        User currentUser = jwtService.getUser();
        List<User> foundFriends = friendsRepository.findUserFriendsWithSearch(currentUser.getId(), searchTerm);
        return foundFriends.stream()
                .map(friend -> new PreviewUserDTO(friend.getId(), friend.getEmail()))
                .toList();
    }

    public List<FriendRequestDTO> getAllPendingRequestsForUser() throws ResourceNotFoundException {
        User currentUser = jwtService.getUser();
        return friendRequestRepository.getPendingForUser(currentUser.getId())
                .stream()
                .map(friendRequestMapper::entityToDTO)
                .toList();
    }

    @Transactional
    public ResolvedFriendRequestDTO acceptRequest(Integer friendRequestId) throws ResourceNotFoundException {
        User currentUser = jwtService.getUser();

        FriendRequest friendRequest = friendRequestRepository.findByIdAndTo_Id(friendRequestId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERROR_FINDING_FRIEND_REQUEST, "Friend request id and current user do not match"));

        friendRequestRepository.deleteById(friendRequestId);

        Friends friendsEntity = friendsMapper.friendsEntityFromUsers(friendRequest.getFrom(), friendRequest.getTo());
        friendsRepository.save(friendsEntity);
        return new ResolvedFriendRequestDTO("Successfully became friends with: " + friendsEntity.getFriend().getEmail());
    }

    public ResolvedFriendRequestDTO declineRequest(Integer friendRequestId) throws ResourceNotFoundException {
        User currentUser = jwtService.getUser();

        FriendRequest friendRequest = friendRequestRepository.findByIdAndTo_Id(friendRequestId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERROR_FINDING_FRIEND_REQUEST, "Friend request id and current user do not match"));

        friendRequestRepository.deleteById(friendRequestId);

        return new ResolvedFriendRequestDTO("Successfully declined a request with: " + friendRequest.getFrom().getEmail());
    }

    public void deleteFriend(Integer friendId) throws ResourceNotFoundException, BusinessLogicException {
        userRepository.findById(friendId).
                orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERROR_FINDING_USER, "User not found"));

        User currentUser = jwtService.getUser();
        Friends friendsEntity = friendsRepository.areTwoUsersFriends(currentUser.getId(), friendId).
                orElseThrow(() -> new BusinessLogicException(ErrorCode.ERROR_USERS_NOT_FRIENDS, "You are not friends with this user"));

        friendsRepository.deleteById(friendsEntity.getId());

    }
}
