package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.dto.post.UpdatePostDTO;
import com.socialnetwork.socialnetwork.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/posts")
public class PostController {

    @Autowired
    private PostService postService;


    @PostMapping("/{idOwner}")
    public void save(@PathVariable Integer idOwner,  @RequestBody CreatePostDTO postDTO) {
        postService.createPost(idOwner, postDTO);
    }

    @PutMapping("/{idUser}")
    public void update(@PathVariable Integer idUser, @RequestBody UpdatePostDTO postDTO){
        postService.updatePost(idUser, postDTO);
    }
}
