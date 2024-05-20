package com.socialnetwork.socialnetwork.repository;

import org.springframework.stereotype.Component;

@Component
public class QueryConstants {


    public static final String REQUEST_EXIST_BETWEEN_USERS = "SELECT fr FROM FriendRequest fr WHERE (fr.from.id = :idUser1 AND fr.to.id = :idUser2) OR (fr.from.id = :idUser2 AND fr.to.id = :idUser1)";
    public static final String ARE_TWO_USERS_FRIENDS = "SELECT fr FROM Friends fr WHERE (fr.friendTo.id = :idUser1 AND fr.friend.id = :idUser2) OR (fr.friendTo.id = :idUser2 AND fr.friend.id = :idUser1)";

}
