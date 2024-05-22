package com.socialnetwork.socialnetwork.repository;

import org.springframework.stereotype.Component;

@Component
public class QueryConstants {


    public static final String REQUEST_EXIST_BETWEEN_USERS = "SELECT fr FROM FriendRequest fr WHERE (fr.from.id = :idUser1 AND fr.to.id = :idUser2) OR (fr.from.id = :idUser2 AND fr.to.id = :idUser1)";
    public static final String ARE_TWO_USERS_FRIENDS = "SELECT fr FROM Friends fr WHERE (fr.friendTo.id = :idUser1 AND fr.friend.id = :idUser2) OR (fr.friendTo.id = :idUser2 AND fr.friend.id = :idUser1)";
    public static final String USER_FRIENDS_SEARCH = "SELECT u FROM User u " +
            "JOIN Friends f ON u.id = f.friendTo.id OR u.id = f.friend.id " +
            "WHERE (f.friendTo.id = :userId OR f.friend.id = :userId) " +
            "AND u.email LIKE %:searchTerm% " +
            "AND u.id != :userId";

    public static final String ALL_PENDING_FOR_USER = "SELECT fr FROM FriendRequest fr WHERE fr.to.id = :userId";

    public static final String ADMIN_AND_GROUP_TUPLE_EXISTS = "SELECT CASE " +
            "WHEN count(*) > 0 THEN TRUE " +
            "ELSE FALSE END " +
            "FROM Group g WHERE (g.admin.id = :idAdmin AND g.id = :idGroup)";


}
