package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply,Integer> {}
