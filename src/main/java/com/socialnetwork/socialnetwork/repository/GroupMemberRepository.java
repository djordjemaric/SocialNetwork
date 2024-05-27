package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {
    public static final String queryText = "SELECT CASE " +
            "WHEN count(*) > 0 THEN TRUE ELSE FALSE END " +
            "FROM GroupMember gm WHERE (gm.member.id = :idUser AND gm.group.id = :idGroup)";

    @Query(value = queryText)
    boolean existsByUserIdAndGroupId(Integer idUser, Integer idGroup);

    Optional<GroupMember> findByMemberAndGroup(User member, Group group);

    void deleteGroupMemberByGroupIdAndMemberId(Integer idGroup, Integer idUser);


}
