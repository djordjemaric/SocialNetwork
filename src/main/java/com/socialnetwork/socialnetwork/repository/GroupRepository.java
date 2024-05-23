package com.socialnetwork.socialnetwork.repository;


import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.socialnetwork.socialnetwork.repository.QueryConstants.ADMIN_AND_GROUP_TUPLE_EXISTS;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    boolean existsByAdmin(User user);
    boolean existsByIdAndAdminId(Integer idGroup, Integer idAdmin);

    boolean existsByName(String name);

    List<Group> findAllByNameStartingWith(String name);

    @Query(value = ADMIN_AND_GROUP_TUPLE_EXISTS)
    boolean existsByAdminIdAndGroupId(Integer idAdmin, Integer idGroup);


}
