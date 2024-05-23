package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {
    Optional<Comment> findByIdAndPost_Id(Integer commentId, Integer postId);
}
