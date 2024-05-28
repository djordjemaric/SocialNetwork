package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.IntegrationTestConfiguration;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.repository.PostRepository;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PostControllerTests extends IntegrationTestConfiguration {

    private final String postApiURL="/api/posts";


    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void emptyDatabase(){
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void does_getById_return_postDTO() throws ResourceNotFoundException {
        User currentUser=jwtService.getUser();
        User testOwner = new User();
        testOwner.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testOwner.setEmail("xanitev711@mcatag.com");
        testOwner = userRepository.save(testOwner);
        Post post = new Post();
        post.setOwner(testOwner);
        post.setPublic(false);
        post=postRepository.save(post);
    }



//    @Test
//    public void testGetById_privatePost_notFriends_throwsAccessDeniedException() throws Exception {
//        // Setup test data
////        User currentUser=jwtService.getUser();
//        User testOwner = new User();
//        testOwner.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
//        testOwner.setEmail("xanitev711@mcatag.com");
//        testOwner = userRepository.save(testOwner);
//        Post post = new Post();
//        post.setOwner(testOwner);
//        post.setPublic(false);
//        post=postRepository.save(post);
//
//        when(jwtService.getUser()).thenReturn(testOwner);
//
//        Post finalPost = post;
//        assertThrows(AccessDeniedException.class, () -> {
//            postService.getById(finalPost.getId());
//        });
//    }
}
