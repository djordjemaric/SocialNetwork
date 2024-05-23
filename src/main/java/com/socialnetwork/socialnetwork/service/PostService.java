package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.dto.post.OpenAIPostDTO;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.dto.post.UpdatePostDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.PostMapper;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.repository.PostRepository;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
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
    private final ChatClient chatClient;

    public PostService(PostRepository postRepository, PostMapper postMapper, GroupRepository groupRepository, JwtService jwtService, S3Service s3Service, ChatClient chatClient) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.groupRepository = groupRepository;
        this.jwtService = jwtService;
        this.s3Service = s3Service;
        this.chatClient = chatClient;
    }
    private String uploadImageAndGetKey(MultipartFile image) {
        if(image==null){
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

    public PostDTO createPostInGroup(CreatePostDTO postDTO) {
        User user = jwtService.getUser();

        Group group = groupRepository.findById(postDTO.idGroup()).orElseThrow(
                () -> new NoSuchElementException("There is no group with the id of " + postDTO.idGroup()));

        String imgS3Key = uploadImageAndGetKey(postDTO.img());
        Post post = postMapper.createPostDTOtoPostInGroup(user, group, imgS3Key, postDTO);
        post = postRepository.save(post);
        return postMapper.postToPostDTO(post);
    }

    public PostDTO createPostOnTimeline(CreatePostDTO postDTO) {
        User user = jwtService.getUser();
        String imgS3Key = uploadImageAndGetKey(postDTO.img());
        Post post = postMapper.createPostDTOtoPostOnTimeline(user, imgS3Key, postDTO);
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

    public PostDTO createAIPostOnTimeline(OpenAIPostDTO postDTO) {
        User user=jwtService.getUser();
        String generatedText= chatClient.call(new Prompt(postDTO.txtPrompt())).getResult().getOutput().getContent();
        Post post=postMapper.OpenAIPostDTOtoPostOnTimeline(postDTO,user,generatedText);
        post=postRepository.save(post);
        return postMapper.postToPostDTO(post);
    }

    public PostDTO createAIPostInGroup(OpenAIPostDTO postDTO) {
        User user = jwtService.getUser();
        Group group = groupRepository.findById(postDTO.idGroup()).orElseThrow(
                () -> new NoSuchElementException("There is no group with the id of " + postDTO.idGroup()));
        String generatedText= chatClient.call(new Prompt(postDTO.txtPrompt())).getResult().getOutput().getContent();
        Post post=postMapper.OpenAIPostDTOtoPostInGroup(postDTO,user,group,generatedText);
        post=postRepository.save(post);
        return postMapper.postToPostDTO(post);
    }
}
