package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.FriendRequest;
import com.socialnetwork.socialnetwork.entity.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendsRepository extends JpaRepository<FriendRequest,Integer> {
    @Query(QueryConstants.ARE_TWO_USERS_FRIENDS)
    Optional<Friends> areTwoUsersFriends(Integer idUser1, Integer idUser2);
}
