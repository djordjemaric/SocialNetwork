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

    private PostRepository postRepository;
    private PostMapper postMapper;
    private GroupRepository groupRepository;

    public PostService(PostRepository postRepository, PostMapper postMapper, GroupRepository groupRepository) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.groupRepository = groupRepository;
    }

    public void createPost(Integer idOwner, CreatePostDTO postDTO) {
        Post post;
        if (postDTO.idGroup() != null) {
            Group group = groupRepository.findById(postDTO.idGroup()).orElseThrow(
                    ()->new NoSuchElementException("There is no group with the id of "+postDTO.idGroup()));
            post = postMapper.createPostDTOtoPost(idOwner, group, postDTO);
            postRepository.save(post);
        }
       postRepository.save(postMapper.createPostDTOtoPost(idOwner, null, postDTO));
    }

    public void updatePost(Integer idUser, UpdatePostDTO updatePostDTO){
        Post post=postRepository.findById(updatePostDTO.id()).orElseThrow();
        if(!(Objects.equals(post.getOwner().getId(), idUser))){
            throw new RuntimeException("User is not the owner!");
        }
        post.setText(updatePostDTO.text());
        post.setImgUrl(updatePostDTO.imgUrl());
        post.setPublic(updatePostDTO.isPublic());
        postRepository.save(post);
    }


}
