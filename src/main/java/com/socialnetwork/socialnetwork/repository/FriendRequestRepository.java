package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {


    @Query(QueryConstants.REQUEST_EXIST_BETWEEN_USERS)
    Optional<FriendRequest> doesRequestExistsBetweenUsers(Integer idUser1, Integer idUser2);
}
