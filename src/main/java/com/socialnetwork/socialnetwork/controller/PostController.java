package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
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


    @PostMapping("/{idOwner}")
    public void save(@PathVariable Integer idOwner, @RequestBody CreatePostDTO postDTO) {
       postService.createPost(idOwner, postDTO);
    }

    @PutMapping("post/{idUser}/{idPost}")
    public void update(@PathVariable Integer idUser, @PathVariable Integer idPost, @RequestBody UpdatePostDTO postDTO){
        postService.updatePost(idUser, idPost, postDTO);
    }
}
