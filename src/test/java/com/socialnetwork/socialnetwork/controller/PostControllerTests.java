package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.IntegrationTestConfiguration;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.repository.PostRepository;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PostControllerTests extends IntegrationTestConfiguration {

    private String postApiURL="api/posts";

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    //napravi after all

    @Test
    public void testGetById_privatePost_notFriends_throwsAccessDeniedException() throws Exception {
        // Setup test data
        User currentUser = jwtService.getUser();
        User testOwner = new User();
        testOwner.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testOwner.setEmail("xanitev711@mcatag.com");
        testOwner = userRepository.save(testOwner);
        Post post = new Post();
        post.setOwner(testOwner);
        post.setPublic(false);
        post=postRepository.save(post);

        PostDTO postDTO=restTemplate.getForObject(postApiURL+"/"+post.getId(), PostDTO.class);

        assertThat()
    }

    @Test
    public void testGetById_GroupPost_NotMember_ThrowsAccessDeniedException() throws Exception {
        // Setup test data
        User user = createUser();
        Group group = createPrivateGroup();
        User owner = createUser("owner");
        Post post = createGroupPost(owner, group);
        saveEntities(user, owner, group, post);
       }

    // Helper methods for creating test data
    private Post createPublicPost(User owner) {
        Post post = new Post();
        post.setOwner(owner);
        post.setPublic(true);
        return postRepository.save(post);
    }

    private Post createPrivatePost(User owner) {
        Post post = new Post();
        post.setOwner(owner);
        post.setPublic(false);
        return postRepository.save(post);
    }

    private Group createPrivateGroup() {
        Group group = new Group();
        group.setName("Private Group");
        group.setPublic(false);
        return groupRepository.save(group);
    }

    private Post createGroupPost(User owner, Group group) {
        Post post = new Post();
        post.setOwner(owner);
        post.setGroup(group);
        return postRepository.save(post);
    }

    private void saveEntities(Object... entities) {
        for (Object entity : entities) {
            if (entity instanceof User) {
                userRepository.save((User) entity);
            } else if (entity instanceof Post) {
                postRepository.save((Post) entity);
            } else if (entity instanceof Group) {
                groupRepository.save((Group) entity);
            }
        }
    }


}
