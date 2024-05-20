package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupRequestRepository extends JpaRepository<GroupRequest, Integer> {


}

