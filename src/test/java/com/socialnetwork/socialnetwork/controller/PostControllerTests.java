package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.IntegrationTestConfiguration;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.ErrorCode;
import com.socialnetwork.socialnetwork.exceptions.ExceptionResponse;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.repository.PostRepository;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PostControllerTests extends IntegrationTestConfiguration {

    private final String POSTS_URL = "/api/posts";

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    @AfterEach
    void cleanDatabase() {
        postRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("Testing if user gets the post he asked for")
    public void doesGetByIdReturnTheCorrectPost() throws ResourceNotFoundException {
        User currentUser = jwtService.getUser();

        Post post = new Post();
        post.setOwner(currentUser);
        post.setPublic(false);
        post = postRepository.save(post);

        ResponseEntity<PostDTO> postDTO = restTemplate.getForEntity(POSTS_URL + "/" + post.getId(), PostDTO.class);

        assertThat(postDTO.getBody().id()).isEqualTo(post.getId());
    }

    @Test
    @DisplayName("Error while getting post that doesn't exist")
    void gettingPostThatIsNotPresent() throws ResourceNotFoundException {
        ResponseEntity<ExceptionResponse> response = restTemplate.exchange(POSTS_URL + "/5", HttpMethod.GET, null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_FINDING_POST);
        assertThat(response.getBody().message()).isEqualTo("The post with the id of 5 is not present in the database.");
    }

    @Test
    @DisplayName("Error while user who is not the friend of the owner or the owner is getting the post")
    void gettingPostWithoutAccess() throws ResourceNotFoundException {
        User testUser1 = new User();
        testUser1.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser1.setEmail("xanitev711@mcatag.com");
        userRepository.save(testUser1);

        Post post = new Post();
        post.setOwner(testUser1);
        post.setPublic(false);
        post=postRepository.save(post);

        ResponseEntity<ExceptionResponse> response = restTemplate.exchange(POSTS_URL + "/" + post.getId(), HttpMethod.GET, null, ExceptionResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("You cannot see the post because you are not friends with the post owner.");
    }

    @Test
    @DisplayName("Error while user who is not a member of a group is getting a post from a private group")
    void gettingPostFromPrivateGroup() throws ResourceNotFoundException {
        User testUser1 = new User();
        testUser1.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser1.setEmail("xanitev711@mcatag.com");
        userRepository.save(testUser1);

        Group group = new Group();
        group.setName("testGroup");
        group.setAdmin(testUser1);
        group.setPublic(false);
        group = groupRepository.save(group);

        Post post = new Post();
        post.setOwner(testUser1);
        post.setGroup(group);
        post = postRepository.save(post);

        ResponseEntity<ExceptionResponse> response = restTemplate.exchange(POSTS_URL + "/" + post.getId(), HttpMethod.GET, null, ExceptionResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("You cannot see the post because you are not a member of the testGroup group.");
    }


    @Test
    @DisplayName("Testing if user-generated post can be created successfully")
    public void isPostSaved() throws ResourceNotFoundException {
        User currentUser = jwtService.getUser();

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("isPublic", "false");
        formData.add("text", "Lorem ipsum");
        formData.add("img", null);
        formData.add("idGroup", null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<PostDTO> postDTO = restTemplate.postForEntity(POSTS_URL, requestEntity, PostDTO.class);

        assertThat(postDTO.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(postDTO.getBody().text()).isEqualTo("Lorem ipsum");
        assertThat(postDTO.getBody().imgUrl()).isEqualTo("");
        assertThat(postDTO.getBody().userEmail()).isEqualTo(currentUser.getEmail());
        assertThat(postDTO.getBody().groupName()).isNull();
        assertThat(postDTO.getBody().comments()).isNull();
    }

    @Test
    @DisplayName("Error while user tries to create a post in a group that doesn't exist")
    void creatingPostInGroupThatIsNotPresent() throws ResourceNotFoundException {

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("isPublic", "false");
        formData.add("text", "Lorem ipsum");
        formData.add("img", null);
        formData.add("idGroup", 5);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<ExceptionResponse> response = restTemplate.exchange(POSTS_URL, HttpMethod.POST, requestEntity, ExceptionResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_CREATING_POST);
        assertThat(response.getBody().message()).isEqualTo("The group in which you tried to create a post doesn't exist.");
    }

    @Test
    @DisplayName("Error while user tries to create a post in a group that they are not a member of")
    void creatingPostInGroupWithoutAccess() throws ResourceNotFoundException {
        User testUser1 = new User();
        testUser1.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser1.setEmail("xanitev711@mcatag.com");
        userRepository.save(testUser1);

        Group group = new Group();
        group.setName("testGroup");
        group.setAdmin(testUser1);
        group.setPublic(false);
        group = groupRepository.save(group);

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("isPublic","false");
        formData.add("text", "Lorem ipsum");
        formData.add("img", null);
        formData.add("idGroup", group.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<ExceptionResponse> response = restTemplate.exchange(POSTS_URL, HttpMethod.POST, requestEntity, ExceptionResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("You cannot create post because you are not a member of this group.");
    }


    @Test
    @DisplayName("Testing if the post can be updated successfully")
    public void isPostUpdated() throws ResourceNotFoundException {
        User currentUser = jwtService.getUser();

        Post post = new Post();
        post.setOwner(currentUser);
        post.setPublic(false);
        post = postRepository.save(post);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("isPublic", "true");
        formData.add("text", "Lorem");
        formData.add("img", null);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<PostDTO> postDTO = restTemplate.exchange(POSTS_URL + "/" + post.getId(), HttpMethod.PUT, requestEntity, PostDTO.class);

        assertThat(postDTO.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(postDTO.getBody().id()).isEqualTo(post.getId());
        assertThat(postDTO.getBody().userEmail()).isEqualTo(currentUser.getEmail());
        assertThat(postDTO.getBody().text()).isEqualTo("Lorem");
        assertThat(postDTO.getBody().imgUrl()).isEqualTo("");
    }

    @Test
    @DisplayName("Error while updating post that doesn't exist")
    public void updatingPostThatIsNotPresent() throws ResourceNotFoundException {
        User currentUser = jwtService.getUser();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("isPublic", "true");
        formData.add("text", "Lorem");
        formData.add("img", null);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<ExceptionResponse> response = restTemplate.exchange(POSTS_URL + "/5", HttpMethod.PUT, requestEntity, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_UPDATING_POST);
        assertThat(response.getBody().message()).isEqualTo("The post which you are trying to update doesn't exist.");
    }

    @Test
    @DisplayName("Error while user who is not the owner is trying to update a post")
    public void updatingPostWithoutAccess() throws ResourceNotFoundException {
        User testUser1 = new User();
        testUser1.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser1.setEmail("xanitev711@mcatag.com");
        userRepository.save(testUser1);

        Post post = new Post();
        post.setOwner(testUser1);
        post.setPublic(false);
        post = postRepository.save(post);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("isPublic", "true");
        formData.add("text", "Lorem");
        formData.add("img", null);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<ExceptionResponse> response = restTemplate.exchange(POSTS_URL + "/" + post.getId(), HttpMethod.PUT, requestEntity, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Only the owner can update this post!");
    }


    @Test
    @DisplayName("Testing if post can be deleted")
    public void isPostDeleted() throws ResourceNotFoundException {
        User currentUser = jwtService.getUser();

        Post post = new Post();
        post.setOwner(currentUser);
        post.setPublic(false);
        post = postRepository.save(post);

        ResponseEntity<Void> response = restTemplate.exchange(POSTS_URL + "/" + post.getId(), HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Error while deleting post that doesn't exist")
    public void deletingPostThatIsNotPresent() throws ResourceNotFoundException {

        ResponseEntity<ExceptionResponse> response = restTemplate.exchange(POSTS_URL + "/5", HttpMethod.DELETE, null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_DELETING_POST);
        assertThat(response.getBody().message()).isEqualTo("The post which you are trying to delete doesn't exist.");
    }

    @Test
    @DisplayName("Error while user who is not the owner is trying to delete a post")
    public void deletingPostWithoutAccess() throws ResourceNotFoundException {
        User testUser1 = new User();
        testUser1.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser1.setEmail("xanitev711@mcatag.com");
        userRepository.save(testUser1);

        Post post = new Post();
        post.setOwner(testUser1);
        post.setPublic(false);
        post = postRepository.save(post);

        ResponseEntity<ExceptionResponse> response = restTemplate.exchange(POSTS_URL + "/" + post.getId(), HttpMethod.DELETE, null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("You don't have the permission to delete this post.");
    }


}
