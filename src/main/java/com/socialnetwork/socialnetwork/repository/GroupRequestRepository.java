package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.GroupRequest;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupRequestRepository extends JpaRepository<GroupRequest, Integer> {
    boolean existsByUserIdAndGroupId(Integer idUser,Integer idGroup);
}

