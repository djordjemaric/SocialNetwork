package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRequestRepository extends JpaRepository<GroupRequest, Integer> {
    List<GroupRequest> findAllByGroup(Group group);

    boolean existsByUserAndGroup(User user, Group group);

    boolean existsByGroupId(Integer idGroup);

}

