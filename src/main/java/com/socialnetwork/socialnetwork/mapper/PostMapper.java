package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.dto.post.AIGeneratedPostDTO;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.dto.post.UpdatePostDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.service.S3Service;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PostMapper {

    private final S3Service s3Service;

    public PostMapper(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public Post createPostDTOtoPostInGroup(User owner, Group group, String imgS3Key, CreatePostDTO postDTO) {
        Post post = new Post();

        post.setGroup(group);
        post.setPublic(group.isPublic());
        post.setText(postDTO.text());
        post.setOwner(owner);
        post.setImgS3Key(imgS3Key);

        return post;
    }

    public Post createPostDTOtoPostOnTimeline(User owner, String imgS3Key, CreatePostDTO postDTO) {

        Post post = new Post();

        post.setPublic(postDTO.isPublic());
        post.setText(postDTO.text());
        post.setOwner(owner);
        post.setImgS3Key(imgS3Key);

        return post;
    }

    public Post updatePostDTOtoPost(UpdatePostDTO updatePostDTO, String imgS3Key, Post post) {
        post.setText(updatePostDTO.text());
        post.setPublic(updatePostDTO.isPublic());
        if (imgS3Key != null) {
            post.setImgS3Key(imgS3Key);
        }
        return post;
    }

    public PostDTO postToPostDTO(Post post) {
        String groupName = null;
        if (post.getGroup() != null) {
            groupName = post.getGroup().getName();
        }
        String imgURL = "";
        if (post.getImgS3Key() != null) {
            imgURL = s3Service.createPresignedDownloadUrl(post.getImgS3Key());
        }
        return new PostDTO(
                post.getId(),
                post.getText(),
                imgURL,
                post.getOwner().getEmail(),
                groupName,
                post.getComments());
    }

    public CreatePostDTO AIGeneratedPostToCreatePostDTO(AIGeneratedPostDTO postDTO, String generatedtext, MultipartFile generatedImage){
        return new CreatePostDTO(
                postDTO.isPublic(),
                generatedtext,
                generatedImage,
                postDTO.idGroup()
                );
    }

    public Post AIGeneratedPostDTOtoPostOnTimeline(AIGeneratedPostDTO postDTO, User user, String generatedText, String s3Key) {
        Post post = new Post();
        post.setText(generatedText);
        post.setPublic(postDTO.isPublic());
        post.setImgS3Key(s3Key);
        post.setOwner(user);
        return post;
    }

    public Post AIGeneratedPostDTOtoPostInGroup(AIGeneratedPostDTO postDTO, User user, Group group, String generatedText, String s3Key) {
        Post post = new Post();
        post.setText(generatedText);
        post.setPublic(postDTO.isPublic());
        post.setGroup(group);
        post.setImgS3Key(s3Key);
        post.setOwner(user);
        return post;
    }


}
