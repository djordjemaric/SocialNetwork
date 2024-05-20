package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    List<Group> findAllByNameStartingWith(String name);
    boolean existsAllByNameStartingWith(String name);
    boolean existsByName(String email);


 public static final String adminAndGroupID = "SELECT CASE " +
            "WHEN count(*) > 0 THEN TRUE " +
            "ELSE FALSE END " +
            "FROM Group g WHERE (g.admin.id = :idAdmin AND g.id = :idGroup)";

    @Query( value = adminAndGroupID)
    boolean existsByAdminIdAndGroupId(Integer idAdmin,Integer idGroup);


}
