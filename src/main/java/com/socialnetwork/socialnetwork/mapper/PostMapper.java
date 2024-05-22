package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public PostDTO postToPostDTO(Post post) {
        return new PostDTO(
                post.getId(), post.getText(), post.getImgUrl(), post.getOwner().getEmail(), post.getGroup().getName(), post.getComments());
}

    public Post createPostDTOtoPostInGroup(Integer idOwner, Group group, CreatePostDTO postDTO){
        Post post=new Post();

        User owner=new User();
        owner.setId(idOwner);

        post.setGroup(group);
        post.setPublic(group.isPublic());
        post.setText(postDTO.text());
        post.setImgUrl(postDTO.imgUrl());
        post.setOwner(owner);

        return post;
    }

    public Post createPostDTOtoPostOnTimeline(Integer idOwner, CreatePostDTO postDTO){
        Post post=new Post();

        User owner=new User();
        owner.setId(idOwner);

        post.setPublic(postDTO.isPublic());
        post.setText(postDTO.text());
        post.setImgUrl(postDTO.imgUrl());
        post.setOwner(owner);

        return post;
    }





}
