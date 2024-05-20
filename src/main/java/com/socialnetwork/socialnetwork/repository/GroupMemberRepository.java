package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {

    public static final String queryText = "SELECT CASE " +
            "WHEN count(*) > 0 THEN TRUE ELSE FALSE END " +
            "FROM GroupMember gm WHERE (gm.member.id = :idUser AND gm.group.id = :idGroup)";

    @Query(value = queryText)
    boolean existsByUserIdAndGroupId(Integer idUser,Integer idGroup);

    void deleteGroupMemberByGroupIdAndMemberId(Integer idGroup, Integer idUser);


}
