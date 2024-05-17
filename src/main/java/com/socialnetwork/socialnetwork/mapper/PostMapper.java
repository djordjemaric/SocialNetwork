package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public Post createPostDTOtoPost(Integer idOwner, Group group, CreatePostDTO postDTO){
        Post post=new Post();
        User owner=new User();
        owner.setId(idOwner);

        if(group!=null){
            post.setGroup(group);
            post.setPublic(group.isPublic());
        }else{
            post.setPublic(postDTO.isPublic());
        }
        post.setText(postDTO.text());
        post.setImgUrl(postDTO.imgUrl());
        post.setOwner(owner);

        return post;
    }





}
