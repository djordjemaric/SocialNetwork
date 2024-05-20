package com.socialnetwork.socialnetwork.repository;

import org.springframework.stereotype.Component;

@Component
public class QueryConstants {

    public static final String ALL_REQUESTS_FROM_USER = "SELECT fr FROM FriendRequest fr WHERE fr.from.id = :userId OR fr.to.id = :userId";
    public static final String ARE_TWO_USERS_FRIENDS = "SELECT fr FROM Friends fr WHERE (fr.friendTo.id = :idUser1 AND fr.friend.id = :idUser2) OR (fr.friendTo.id = :idUser2 AND fr.friend.id = :idUser1)";

}
