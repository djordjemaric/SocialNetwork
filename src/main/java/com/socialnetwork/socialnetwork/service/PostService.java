package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.dto.post.UpdatePostDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.mapper.PostMapper;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final GroupRepository groupRepository;

    public PostService(PostRepository postRepository, PostMapper postMapper, GroupRepository groupRepository) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.groupRepository = groupRepository;
    }

    public void createPostInGroup(Integer idOwner, CreatePostDTO postDTO) {
        if((postDTO.text()==null || postDTO.text().trim().isEmpty()) && (postDTO.imgUrl()==null || postDTO.imgUrl().trim().isEmpty())){
            throw new RuntimeException("Post content cannot be empty!");
        }
            Group group = groupRepository.findById(postDTO.idGroup()).orElseThrow(
                    ()->new NoSuchElementException("There is no group with the id of "+postDTO.idGroup()));
            postRepository.save(postMapper.createPostDTOtoPostInGroup(idOwner, group, postDTO));
    }

    public void createPostOnTimeline(Integer idOwner,CreatePostDTO postDTO){
        if((postDTO.text()==null || postDTO.text().trim().isEmpty()) && (postDTO.imgUrl()==null || postDTO.imgUrl().trim().isEmpty())){
            throw new RuntimeException("Post content cannot be empty!");
        }
        postRepository.save(postMapper.createPostDTOtoPostOnTimeline(idOwner, postDTO));
    }

    public void updatePost(Integer idUser, Integer idPost, UpdatePostDTO updatePostDTO){
        if((updatePostDTO.text()==null || updatePostDTO.text().trim().isEmpty()) && (updatePostDTO.imgUrl()==null || updatePostDTO.imgUrl().trim().isEmpty())){
            throw new RuntimeException("Post content cannot be empty!");
        }
        Post post=postRepository.findById(idPost).orElseThrow(()->
                new NoSuchElementException("There is no post with the id of "+idPost));
        if(!(Objects.equals(post.getOwner().getId(), idUser))){
            throw new RuntimeException("User is not the owner!");
        }
        post.setText(updatePostDTO.text());
        post.setImgUrl(updatePostDTO.imgUrl());
        post.setPublic(updatePostDTO.isPublic());
        postRepository.save(post);
    }


}
