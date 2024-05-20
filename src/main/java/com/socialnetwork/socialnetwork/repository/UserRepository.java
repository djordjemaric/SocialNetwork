package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserSub(String userSub);
    Optional<User> findById(Integer id);
    boolean existsByEmail(String email);

}
