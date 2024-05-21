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

    @GetMapping("/{id}")
    public GetPostDTO getById(@PathVariable Integer id) {
        return postService.getById(id);
    }

    @PostMapping
    public void save(@RequestBody CreatePostDTO postDTO) {
        if (postDTO.idGroup() == null) {
            postService.createPostOnTimeline(postDTO);
        } else {
            postService.createPostInGroup(postDTO);
        }
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Integer id, @RequestBody UpdatePostDTO postDTO) {
        postService.updatePost(id, postDTO);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        postService.deletePost(id);
    }


}
