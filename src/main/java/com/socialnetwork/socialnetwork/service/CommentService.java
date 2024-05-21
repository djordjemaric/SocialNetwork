package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.post.CreateCommentDTO;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.CommentMapper;
import com.socialnetwork.socialnetwork.repository.CommentRepository;
import com.socialnetwork.socialnetwork.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;


    public CommentService(CommentRepository commentRepository, PostRepository postRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.commentMapper = commentMapper;
    }

    public void createComment(User owner, CreateCommentDTO commentDTO) {
        Post post = postRepository.findById(commentDTO.idPost()).orElseThrow(
                ()->new NoSuchElementException("There is no post with the id of "+commentDTO.idPost()));
        commentRepository.save(commentMapper.createCommentDTOtoComment(owner,post, commentDTO));
    }
}
