package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    boolean existsByName(String email);


 public static final String adminAndGroupID = "SELECT CASE " +
            "WHEN count(*) > 0 THEN TRUE " +
            "ELSE FALSE END " +
            "FROM Group g WHERE (g.admin.id = :idAdmin AND g.id = :idGroup)";

    @Query( value = adminAndGroupID)
    boolean existsByAdminIdAndGroupId(Integer idAdmin,Integer idGroup);


}
