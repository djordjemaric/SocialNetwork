package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.dto.post.UpdatePostDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.PostMapper;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final GroupRepository groupRepository;
    private final JwtService jwtService;
    private final S3Service s3Service;

    public PostService(PostRepository postRepository, PostMapper postMapper, GroupRepository groupRepository, JwtService jwtService, S3Service s3Service) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.groupRepository = groupRepository;
        this.jwtService = jwtService;
        this.s3Service = s3Service;
    }
    private String uploadImageAndGetKey(MultipartFile image) {
        if (image != null) {
            return s3Service.uploadToBucket(image);
        }
        return null;
    }

    public PostDTO createPostInGroup(CreatePostDTO postDTO) {
        User user = jwtService.getUser();

        Group group = groupRepository.findById(postDTO.idGroup()).orElseThrow(
                () -> new NoSuchElementException("There is no group with the id of " + postDTO.idGroup()));

        String imgS3Key = uploadImageAndGetKey(postDTO.img());
        Post post = postMapper.createPostDTOtoPostInGroup(user.getId(), group, imgS3Key, postDTO);
        post = postRepository.save(post);
        return postMapper.postToPostDTO(post);
    }

    public PostDTO createPostOnTimeline(CreatePostDTO postDTO) {
        User user = jwtService.getUser();
        String imgS3Key = uploadImageAndGetKey(postDTO.img());
        Post post = postMapper.createPostDTOtoPostOnTimeline(user.getId(), imgS3Key, postDTO);
        post = postRepository.save(post);
        return postMapper.postToPostDTO(post);
    }

    public PostDTO updatePost(Integer idPost, UpdatePostDTO updatePostDTO) {
        User user = jwtService.getUser();

        Post post = postRepository.findById(idPost).orElseThrow(() ->
                new NoSuchElementException("There is no post with the id of " + idPost));
        if (!(Objects.equals(post.getOwner().getId(), user.getId()))) {
            throw new RuntimeException("User is not the owner!");
        }

        if (updatePostDTO.img() != null && post.getImgS3Key() != null) {
           s3Service.deleteFromBucket(post.getImgS3Key());
        }

        String imgS3Key = uploadImageAndGetKey(updatePostDTO.img());
        post = postMapper.updatePostDTOtoPost(updatePostDTO, imgS3Key, post);
        post = postRepository.save(post);
        return postMapper.postToPostDTO(post);
    }
}
