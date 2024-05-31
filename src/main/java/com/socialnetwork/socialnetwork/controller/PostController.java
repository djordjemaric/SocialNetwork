package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.post.*;
import com.socialnetwork.socialnetwork.dto.post.CommentDTO;
import com.socialnetwork.socialnetwork.dto.post.CreateCommentDTO;
import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.dto.post.UpdatePostDTO;
import com.socialnetwork.socialnetwork.exceptions.BusinessLogicException;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.service.CommentService;
import com.socialnetwork.socialnetwork.service.PostService;
import com.socialnetwork.socialnetwork.service.ReplyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final ReplyService replyService;


    public PostController(PostService postService, CommentService commentService, ReplyService replyService) {
        this.postService = postService;
        this.commentService = commentService;
        this.replyService = replyService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public PostDTO getById(@PathVariable Integer id) throws ResourceNotFoundException, BusinessLogicException, AccessDeniedException {
        return postService.getById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PostDTO save(@ModelAttribute @Valid CreatePostDTO postDTO) throws ResourceNotFoundException, AccessDeniedException, BusinessLogicException {
        if (postDTO.idGroup() == null) {
            return postService.createPostOnTimeline(postDTO);
        }
        return postService.createPostInGroup(postDTO);
    }

    @PostMapping("/ai-generated")
    public PostDTO save(@RequestBody @Valid AIGeneratedPostDTO postDTO) throws ResourceNotFoundException, BusinessLogicException {
        if (postDTO.idGroup() == null) {
            return postService.createAIPostOnTimeline(postDTO);
        }
        return postService.createAIPostInGroup(postDTO);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public PostDTO update(@PathVariable Integer id, @ModelAttribute @Valid UpdatePostDTO postDTO) throws ResourceNotFoundException, AccessDeniedException, BusinessLogicException {
        return postService.updatePost(id, postDTO);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{idPost}/comments")
    public CommentDTO saveComment(@PathVariable Integer idPost, @RequestBody @Valid CreateCommentDTO commentDTO) throws ResourceNotFoundException, BusinessLogicException {
        return commentService.createComment(idPost, commentDTO);

    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{idPost}/comments/{commentId}/replies")
    public ReplyDTO saveReply(@PathVariable Integer idPost, @PathVariable Integer commentId, @RequestBody @Valid CreateReplyDTO replyDTO) throws ResourceNotFoundException, BusinessLogicException {
        return replyService.createReply(idPost, commentId, replyDTO);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) throws ResourceNotFoundException, AccessDeniedException, BusinessLogicException {
        postService.deletePost(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{idPost}/comments/{idComment}")
    public void deleteComment(@PathVariable Integer idComment) throws ResourceNotFoundException, AccessDeniedException, BusinessLogicException {
        commentService.deletePost(idComment);
    }
}


