package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.post.CreateCommentDTO;
import com.socialnetwork.socialnetwork.entity.Comment;
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
    private final JwtService jwtService;


    public CommentService(CommentRepository commentRepository, PostRepository postRepository, CommentMapper commentMapper, JwtService jwtService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.commentMapper = commentMapper;
        this.jwtService = jwtService;
    }

    public Comment createComment(Integer idPost, CreateCommentDTO commentDTO) {
    Post post = postRepository.findById(idPost).orElseThrow(
            () -> new NoSuchElementException("There is no post with the id of " + idPost));
    User owner = jwtService.getUser();
        Comment comment = commentMapper.createCommentDTOtoComment(owner, post, commentDTO);
        return commentRepository.save(comment);}

}
