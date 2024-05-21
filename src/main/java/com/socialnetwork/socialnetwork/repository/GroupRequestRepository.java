package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import com.socialnetwork.socialnetwork.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupRequestRepository extends JpaRepository<GroupRequest, Integer> {

    List<GroupRequest> findAllByGroup(Group group);

    boolean existsByUserAndGroup(User user, Group group);

    GroupRequest findByUserAndGroup(User user, Group group);


}

