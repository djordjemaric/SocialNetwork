package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.dto.post.AIGeneratedPostDTO;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.dto.post.UpdatePostDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.mapper.PostMapper;
import com.socialnetwork.socialnetwork.repository.FriendsRepository;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
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
    private final AIService aiService;

    public PostService(PostRepository postRepository, PostMapper postMapper, GroupRepository groupRepository, JwtService jwtService, S3Service s3Service, FriendsRepository friendsRepository, GroupMemberRepository groupMemberRepository, AIService aiService) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.groupRepository = groupRepository;
        this.jwtService = jwtService;
        this.friendsRepository = friendsRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.s3Service = s3Service;
        this.aiService = aiService;
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

    public PostDTO getById(Integer idPost) throws ResourceNotFoundException {
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
        return postMapper.postToPostDTO(post);
    }

    public PostDTO createPostInGroup(CreatePostDTO postDTO) throws ResourceNotFoundException {
        User user = jwtService.getUser();

        Group group = groupRepository.findById(postDTO.idGroup()).orElseThrow(
                () -> new NoSuchElementException("There is no group with the id of " + postDTO.idGroup()));

        if (!groupMemberRepository.existsByUserIdAndGroupId(user.getId(), postDTO.idGroup())) {
            throw new RuntimeException("You cannot create post because you are not a member of this group.");
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

    public PostDTO createAIPostOnTimeline(AIGeneratedPostDTO postDTO) throws ResourceNotFoundException {
        String generatedText = aiService.generateText(postDTO.txtPrompt());
        //null prosledjujemo umesto MultipartFile dok se ne napravi metoda u AIServisu
        CreatePostDTO createPostDTO = postMapper.AIGeneratedPostDTOtoCreatePostDTO(postDTO, generatedText, null);
        return createPostOnTimeline(createPostDTO);
    }

    public PostDTO createAIPostInGroup(AIGeneratedPostDTO postDTO) throws ResourceNotFoundException {
        String generatedText = aiService.generateText(postDTO.txtPrompt());
        //null prosledjujemo umesto MultipartFile dok se ne napravi metoda u AIServisu
        CreatePostDTO createPostDTO = postMapper.AIGeneratedPostDTOtoCreatePostDTO(postDTO, generatedText, null);
        return createPostInGroup(createPostDTO);
    }

    public PostDTO updatePost(Integer idPost, UpdatePostDTO updatePostDTO) throws ResourceNotFoundException {
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


    public void deletePost(Integer idPost) throws ResourceNotFoundException {
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
