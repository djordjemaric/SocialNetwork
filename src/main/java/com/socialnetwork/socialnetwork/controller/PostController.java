package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.post.CommentDTO;
import com.socialnetwork.socialnetwork.dto.post.CreateCommentDTO;
import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.dto.post.UpdatePostDTO;
import com.socialnetwork.socialnetwork.entity.Comment;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.service.CommentService;
import com.socialnetwork.socialnetwork.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;


    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @PostMapping
    public PostDTO save(@ModelAttribute CreatePostDTO postDTO) {
        if (postDTO.idGroup() == null) {
            return postService.createPostOnTimeline(postDTO);
        }
        return postService.createPostInGroup(postDTO);
    }

    @PutMapping("{idPost}")
    public PostDTO update(@PathVariable Integer idPost, @ModelAttribute UpdatePostDTO postDTO) {
        return postService.updatePost(idPost, postDTO);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{postId}/comments")
    public CommentDTO saveComment(@PathVariable Integer idPost, @RequestBody CreateCommentDTO commentDTO) {
        return commentService.createComment(idPost,commentDTO);

    }
    
}
