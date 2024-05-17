package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.FriendRequest;
import com.socialnetwork.socialnetwork.entity.Friends;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendsRepository extends JpaRepository<FriendRequest,Integer> {
    @Query("SELECT fr FROM Friends fr WHERE (fr.friendTo.id = :idUser1 OR fr.friend.id = :idUser2) OR (fr.friendTo.id = :idUser2 OR fr.friend.id = :idUser1)")
    Optional<Friends> areTwoUsersFriends(Integer idUser1, Integer idUser2);
}
