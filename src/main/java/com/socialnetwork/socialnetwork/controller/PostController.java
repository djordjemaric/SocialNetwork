package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.post.*;
import com.socialnetwork.socialnetwork.entity.Comment;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.service.CommentService;
import com.socialnetwork.socialnetwork.service.PostService;
import com.socialnetwork.socialnetwork.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    public void save(@RequestBody CreatePostDTO postDTO) {
        if(postDTO.idGroup()==null){
            postService.createPostOnTimeline(postDTO);
        }else{
            postService.createPostInGroup(postDTO);
        }
    }

    @PutMapping("post/{idPost}")
    public void update(@PathVariable Integer idPost, @RequestBody UpdatePostDTO postDTO){
        postService.updatePost(idPost, postDTO);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{postId}/comments")
    public CommentDTO saveComment(@PathVariable Integer idPost, @RequestBody CreateCommentDTO commentDTO) {
        return commentService.createComment(idPost,commentDTO);

    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{postId}/comments/{commentId}/replies")
    public ReplyDTO saveReply(@PathVariable Integer postId,@PathVariable Integer commentId, @RequestBody CreateReplyDTO replyDTO) {
        return replyService.createReply(postId,commentId, replyDTO);
    }

    }
