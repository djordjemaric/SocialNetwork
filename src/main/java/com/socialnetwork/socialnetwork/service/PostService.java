package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.dto.post.UpdatePostDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.mapper.PostMapper;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PostService {

    @Autowired
    PostRepository postRepository;
    @Autowired
    PostMapper postMapper;
    @Autowired
    GroupRepository groupRepository;

    public void createPost(Integer idOwner, CreatePostDTO postDTO) {
        Post post;
        if (postDTO.idGroup() != null) {
            Group group = groupRepository.findById(postDTO.idGroup()).orElseThrow();
            post = postMapper.createPostDTOtoPost(idOwner, group, postDTO);
            postRepository.save(post);
            return;
        }
        post = postMapper.createPostDTOtoPost(idOwner, null, postDTO);
        postRepository.save(post);
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
