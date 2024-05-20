package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.dto.post.GetPostDTO;
import com.socialnetwork.socialnetwork.dto.post.UpdatePostDTO;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("post/{idPost}")
    public GetPostDTO getById(@PathVariable Integer idPost){
        return postService.getById(idPost);
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
}
