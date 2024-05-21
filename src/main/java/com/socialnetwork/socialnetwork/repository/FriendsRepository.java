package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.FriendRequest;
import com.socialnetwork.socialnetwork.entity.Friends;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendsRepository extends JpaRepository<Friends,Integer> {
    @Query(QueryConstants.ARE_TWO_USERS_FRIENDS)
    Optional<Friends> areTwoUsersFriends(Integer idUser1, Integer idUser2);

    @Query(QueryConstants.USER_FRIENDS_SEARCH)
    List<User> findUserFriendsWithSearch(Integer userId, String searchTerm);
}
