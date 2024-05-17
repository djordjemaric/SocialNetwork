package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.FriendRequest;
import org.apache.coyote.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {
    @Query("SELECT fr FROM FriendRequest fr WHERE fr.from.id = :userId OR fr.to.id = :userId")
    List<FriendRequest> getRequestsFromUser(Integer userId);
}
