package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.dto.post.UpdatePostDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.PostMapper;
import com.socialnetwork.socialnetwork.repository.*;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final GroupRepository groupRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final FriendsRepository friendsRepository;
    private final GroupMemberRepository groupMemberRepository;

    public PostService(PostRepository postRepository, PostMapper postMapper, GroupRepository groupRepository, JwtService jwtService, UserRepository userRepository, S3Service s3Service, FriendsRepository friendsRepository, GroupMemberRepository groupMemberRepository) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.groupRepository = groupRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
        this.friendsRepository = friendsRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    public PostDTO createPostInGroup(CreatePostDTO postDTO) {
        User user = jwtService.getUser();
        if (!userRepository.existsByEmail(user.getEmail())) {
            throw new NoSuchElementException("User with the email of " + user.getEmail() + " is not present in the database.");
        }
        Group group = groupRepository.findById(postDTO.idGroup()).orElseThrow(
                () -> new NoSuchElementException("There is no group with the id of " + postDTO.idGroup()));
        Post post = new Post();
        if (postDTO.img() != null) {
            post.setImgS3Url(UUID.randomUUID().toString());
            s3Service.uploadToBucket(post.getImgS3Url(), postDTO.img());
        }
        Post createdPost = postRepository.save(postMapper.createPostDTOtoPostInGroup(user.getId(), group, postDTO, post));
        String imgURL = "";
        if (post.getImgS3Url() != null) {
            imgURL = s3Service.createPresignedDownloadUrl(createdPost.getImgS3Url());
        }
        return postMapper.postToPostDTO(createdPost, imgURL);
    }

    public PostDTO createPostOnTimeline(CreatePostDTO postDTO) {
        User user = jwtService.getUser();
        if (!userRepository.existsByEmail(user.getEmail())) {
            throw new NoSuchElementException("User with the email of " + user.getEmail() + " is not present in the database.");
        }
        Post post = new Post();
        if (postDTO.img() != null) {
            post.setImgS3Url(UUID.randomUUID().toString());
            s3Service.uploadToBucket(post.getImgS3Url(), postDTO.img());
        }
        Post createdPost = postRepository.save(postMapper.createPostDTOtoPostOnTimeline(user.getId(), postDTO, post));
        String imgURL = "";
        if (post.getImgS3Url() != null) {
            imgURL = s3Service.createPresignedDownloadUrl(createdPost.getImgS3Url());
        }
        return postMapper.postToPostDTO(createdPost, imgURL);
    }

    public PostDTO updatePost(Integer idPost, UpdatePostDTO updatePostDTO) {
        User user = jwtService.getUser();
        if (!userRepository.existsByEmail(user.getEmail())) {
            throw new NoSuchElementException("User with the email of " + user.getEmail() + " is not present in the database.");
        }
        Post post = postRepository.findById(idPost).orElseThrow(() ->
                new NoSuchElementException("There is no post with the id of " + idPost));
        if (!(Objects.equals(post.getOwner().getId(), user.getId()))) {
            throw new RuntimeException("User is not the owner!");
        }
        post.setText(updatePostDTO.text());
        if (updatePostDTO.img() != null) {
            post.setImgS3Url(UUID.randomUUID().toString());
            s3Service.uploadToBucket(post.getImgS3Url(), updatePostDTO.img());
        }
        post.setPublic(updatePostDTO.isPublic());
        post = postRepository.save(post);
        String imgURL = "";
        if (post.getImgS3Url() != null) {
            imgURL = s3Service.createPresignedDownloadUrl(post.getImgS3Url());
        }
        return postMapper.postToPostDTO(post, imgURL);
    }


    public void deletePost(Integer idPost) {
        Post post = postRepository.findById(idPost).orElseThrow(() ->
                new NoSuchElementException("There is no post with the id of " + idPost));
        User user = jwtService.getUser();
        if (post.getGroup() != null) {
            if (Objects.equals(post.getGroup().getAdmin().getId(), user.getId())) {
                postRepository.deleteById(idPost);
                return;
            }
        }
        if (Objects.equals(user.getId(), post.getOwner().getId())) {
            postRepository.deleteById(idPost);
            return;
        }
        throw new RuntimeException("You don't have the permission to delete the post.");
    }
}
