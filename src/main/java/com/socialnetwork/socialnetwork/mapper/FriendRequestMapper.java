package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.friendRequest.PreviewFriendRequestDTO;
import com.socialnetwork.socialnetwork.entity.FriendRequest;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class FriendRequestMapper {

    public FriendRequest friendRequestFromUsers(User userSent, User userReceiver){
        FriendRequest friendRequest = new FriendRequest();

        friendRequest.setFrom(userSent);
        friendRequest.setTo(userReceiver);

        return friendRequest;
    }

    public PreviewFriendRequestDTO entityToPreviewDTO(FriendRequest friendRequest){
        return new PreviewFriendRequestDTO(friendRequest.getFrom().getEmail(), friendRequest.getTo().getEmail());
    }

}
