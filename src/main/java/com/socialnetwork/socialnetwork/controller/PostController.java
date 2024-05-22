package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.dto.post.UpdatePostDTO;
import com.socialnetwork.socialnetwork.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public PostDTO getById(@PathVariable Integer id) {
        return postService.getById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PostDTO save(@RequestBody CreatePostDTO postDTO) {
        if (postDTO.idGroup() == null) {
            return postService.createPostOnTimeline(postDTO);
        }
        return postService.createPostInGroup(postDTO);

    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public PostDTO update(@PathVariable Integer id, @RequestBody UpdatePostDTO postDTO) {
        return postService.updatePost(id, postDTO);
    }
}
