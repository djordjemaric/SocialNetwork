package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.post.CreatePostDTO;
import com.socialnetwork.socialnetwork.dto.post.GetPostDTO;
import com.socialnetwork.socialnetwork.dto.post.UpdatePostDTO;
import com.socialnetwork.socialnetwork.entity.Friends;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.PostMapper;
import com.socialnetwork.socialnetwork.repository.FriendsRepository;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.repository.PostRepository;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final GroupRepository groupRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;

    public PostService(PostRepository postRepository, PostMapper postMapper, GroupRepository groupRepository, JwtService jwtService, UserRepository userRepository, FriendsRepository friendsRepository) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.groupRepository = groupRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.friendsRepository = friendsRepository;
    }

    public GetPostDTO getById(Integer idPost) {
        User user = jwtService.getUser();
        if (!userRepository.existsByEmail(user.getEmail())) {
            throw new NoSuchElementException("User with the email of " + user.getEmail() + " is not present in the database.");
        }
        Post post = postRepository.findById(idPost).orElseThrow(
                () -> new NoSuchElementException("The post with the id of " + idPost + " is not present in the database."));
        if (!post.isPublic() && post.getGroup()!=null) {
            Friends friends = friendsRepository.areTwoUsersFriends(post.getOwner().getId(), user.getId())
                    .orElseThrow(() -> new RuntimeException("You cannot see the post because you are not friends with the post owner."));
        }
        if(post.getGroup()!=null && !(post.getGroup().isPublic())){
            //ODAVDE
        }
        return null;
    }

    public void createPostInGroup(CreatePostDTO postDTO) {
        User user = jwtService.getUser();
        if (!userRepository.existsByEmail(user.getEmail())) {
            throw new NoSuchElementException("User with the email of " + user.getEmail() + " is not present in the database.");
        }
        Group group = groupRepository.findById(postDTO.idGroup()).orElseThrow(
                () -> new NoSuchElementException("There is no group with the id of " + postDTO.idGroup()));
        postRepository.save(postMapper.createPostDTOtoPostInGroup(user.getId(), group, postDTO));
    }

    public void createPostOnTimeline(CreatePostDTO postDTO) {
        User user = jwtService.getUser();
        if (!userRepository.existsByEmail(user.getEmail())) {
            throw new NoSuchElementException("User with the email of " + user.getEmail() + " is not present in the database.");
        }
        postRepository.save(postMapper.createPostDTOtoPostOnTimeline(user.getId(), postDTO));
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
        post.setImgUrl(updatePostDTO.imgUrl());
        post.setPublic(updatePostDTO.isPublic());
        postRepository.save(post);
    }


}
