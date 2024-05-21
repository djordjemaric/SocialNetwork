package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.entity.Friends;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.stereotype.Component;

@Component
public class FriendsMapper {

    public Friends friendsEntityFromUsers(User userSent, User userReceived){
        Friends friendsEntity = new Friends();
        friendsEntity.setFriend(userSent);
        friendsEntity.setFriendTo(userReceived);

        return friendsEntity;
    }

}
