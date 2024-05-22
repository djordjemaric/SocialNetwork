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
import org.springframework.web.multipart.MultipartFile;

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

    public void createPostInGroup(CreatePostDTO postDTO) {
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
        postRepository.save(postMapper.createPostDTOtoPostInGroup(user.getId(), group, postDTO, post));
    }

    public void createPostOnTimeline(CreatePostDTO postDTO) {
        User user = jwtService.getUser();
        if (!userRepository.existsByEmail(user.getEmail())) {
            throw new NoSuchElementException("User with the email of " + user.getEmail() + " is not present in the database.");
        }
        Post post = new Post();
        if (postDTO.img() != null) {
            post.setImgS3Url(UUID.randomUUID().toString());
            s3Service.uploadToBucket(post.getImgS3Url(),postDTO.img());
        }
        postRepository.save(postMapper.createPostDTOtoPostOnTimeline(user.getId(), postDTO, post));
    }

    public void updatePost(Integer idPost, UpdatePostDTO updatePostDTO) {
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
        postRepository.save(post);
    }

    public PostDTO getById(Integer idPost) {
        User user = jwtService.getUser();
        Post post = postRepository.findById(idPost)
                .orElseThrow(() -> new NoSuchElementException("The post with the id of " +
                idPost + " is not present in the database."));

        if (!post.isPublic() && post.getGroup() == null) {
            if (friendsRepository.areTwoUsersFriends(post.getOwner().getId(), user.getId()).isEmpty()) {
                throw new RuntimeException("You cannot see the post because you are not friends with the post owner.");
            }
        }

        if (post.getGroup() != null && !(post.getGroup().isPublic())) {
            if (!(groupMemberRepository.existsByUserIdAndGroupId(user.getId(), post.getGroup().getId()))) {
                throw new RuntimeException("You cannot see the post because you are not a member of the "
                        + post.getGroup().getName() + " group.");
            }
        }
        String imageUrl = "";
        if (post.getImgS3Url() != null) {
            imageUrl = s3Service.createPresignedDownloadUrl(post.getImgS3Url());
        }

        return postMapper.postToPostDTO(post, imageUrl);

        }
}
