package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import static com.socialnetwork.socialnetwork.repository.QueryConstants.ADMIN_AND_GROUP_TUPLE_EXISTS;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    boolean existsByName(String email);

    @Query( value = ADMIN_AND_GROUP_TUPLE_EXISTS)
    boolean existsByAdminIdAndGroupId(Integer idAdmin,Integer idGroup);


}
