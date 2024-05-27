package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.dto.post.UpdatePostDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.BusinessLogicException;
import com.socialnetwork.socialnetwork.exceptions.ErrorCode;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.mapper.PostMapper;
import com.socialnetwork.socialnetwork.repository.FriendsRepository;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.repository.PostRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final GroupRepository groupRepository;
    private final JwtService jwtService;
    private final S3Service s3Service;
    private final FriendsRepository friendsRepository;
    private final GroupMemberRepository groupMemberRepository;

    public PostService(PostRepository postRepository, PostMapper postMapper, GroupRepository groupRepository, JwtService jwtService, S3Service s3Service, FriendsRepository friendsRepository, GroupMemberRepository groupMemberRepository) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.groupRepository = groupRepository;
        this.jwtService = jwtService;
        this.friendsRepository = friendsRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.s3Service = s3Service;
    }

    private String uploadImageAndGetKey(MultipartFile image) {
        if (image == null) {
            return null;
        }
        String filename = image.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        String extension = filename
                .substring(filename.lastIndexOf("."));

        try (InputStream inputStream = image.getInputStream()) {
            return s3Service.uploadToBucket(extension, inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PostDTO getById(Integer idPost) throws ResourceNotFoundException, AccessDeniedException {
        User user = jwtService.getUser();
        Post post = postRepository.findById(idPost)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERROR_FINDING_POST, "The post with the id of " +
                        idPost + " is not present in the database."));

        if (!post.isPublic() && post.getGroup() == null) {
            if (friendsRepository.areTwoUsersFriends(post.getOwner().getId(), user.getId()).isEmpty()) {
                throw new AccessDeniedException("You cannot see the post because you are not friends with the post owner.");
            }
        }

        if (post.getGroup() != null && !(post.getGroup().isPublic())) {
            if (!(groupMemberRepository.existsByUserIdAndGroupId(user.getId(), post.getGroup().getId()))) {
                throw new AccessDeniedException("You cannot see the post because you are not a member of the "
                        + post.getGroup().getName() + " group.");
            }
        }
        return postMapper.postToPostDTO(post);
    }

    public PostDTO createPostInGroup(CreatePostDTO postDTO) throws ResourceNotFoundException, AccessDeniedException, BusinessLogicException {
        User user = jwtService.getUser();

        Group group = groupRepository.findById(postDTO.idGroup()).orElseThrow(
                () -> new BusinessLogicException(ErrorCode.ERROR_CREATING_POST, "The group in which you tried to create a post doesn't exist."));

        if (!groupMemberRepository.existsByUserIdAndGroupId(user.getId(), postDTO.idGroup())) {
            throw new AccessDeniedException("You cannot create post because you are not a member of this group.");
        }
        String imgS3Key = uploadImageAndGetKey(postDTO.img());
        Post post = postMapper.createPostDTOtoPostInGroup(user, group, imgS3Key, postDTO);
        post = postRepository.save(post);
        return postMapper.postToPostDTO(post);
    }

    public PostDTO createPostOnTimeline(CreatePostDTO postDTO) throws ResourceNotFoundException {
        User user = jwtService.getUser();
        String imgS3Key = uploadImageAndGetKey(postDTO.img());
        Post post = postMapper.createPostDTOtoPostOnTimeline(user, imgS3Key, postDTO);
        post = postRepository.save(post);
        return postMapper.postToPostDTO(post);
    }

    public PostDTO updatePost(Integer idPost, UpdatePostDTO updatePostDTO) throws ResourceNotFoundException, AccessDeniedException, BusinessLogicException {
        User user = jwtService.getUser();

        Post post = postRepository.findById(idPost).orElseThrow(() ->
                new BusinessLogicException(ErrorCode.ERROR_UPDATING_POST, "The post which you are trying to update doesn't exist."));
        if (!(Objects.equals(post.getOwner().getId(), user.getId()))) {
            throw new AccessDeniedException("Only the owner can update this post!");
        }

        if (updatePostDTO.img() != null && post.getImgS3Key() != null) {
            s3Service.deleteFromBucket(post.getImgS3Key());
        }

        String imgS3Key = uploadImageAndGetKey(updatePostDTO.img());
        post = postMapper.updatePostDTOtoPost(updatePostDTO, imgS3Key, post);
        post = postRepository.save(post);
        return postMapper.postToPostDTO(post);
    }


    public void deletePost(Integer idPost) throws ResourceNotFoundException, AccessDeniedException, BusinessLogicException {
        Post post = postRepository.findById(idPost).orElseThrow(() ->
                new BusinessLogicException(ErrorCode.ERROR_DELETING_POST, "The post which you are trying to delete doesn't exist."));
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
        throw new AccessDeniedException("You don't have the permission to delete this post.");
    }
}
