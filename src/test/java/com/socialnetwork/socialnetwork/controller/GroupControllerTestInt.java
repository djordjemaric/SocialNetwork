package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.IntegrationTestConfiguration;
import com.socialnetwork.socialnetwork.dto.group.*;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.entity.*;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GroupControllerTestInt extends IntegrationTestConfiguration {

    private final String groupsApiURL = "/api/groups";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private GroupRequestRepository groupRequestRepository;

    @AfterEach
    void cleanupDatabase() {
        groupRepository.deleteAll();
        userRepository.deleteAll();
        groupMemberRepository.deleteAll();
        groupRequestRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Order(1)
    @DisplayName("Successfully creating group")
    @Test
    void successfullyCreatingGroup() throws ResourceNotFoundException {
        User user = jwtService.getUser();
        userRepository.save(user);

        CreateGroupDTO createGroupDTO = new CreateGroupDTO("Test", false);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).toUriString();

        ResponseEntity<GroupDTO> response = restTemplate.postForEntity(urlWithParams, createGroupDTO, GroupDTO.class);

        assertThat(response.getBody().name()).isEqualTo(createGroupDTO.name());
        assertThat(response.getBody().isPublic()).isEqualTo(createGroupDTO.isPublic());
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Order(2)
    @DisplayName("Successfully deleting group")
    @Test
    void successfullyDeletingGroup() throws ResourceNotFoundException {
        User user = jwtService.getUser();
        userRepository.save(user);

        User testUser = new User();
        testUser.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser.setEmail("xanitev711@mcatag.com");
        testUser = userRepository.save(testUser);

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setAdmin(user);
        testGroup = groupRepository.save(testGroup);

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(user);
        groupMember.setGroup(testGroup);
        groupMemberRepository.save(groupMember);

        GroupRequest testGroupRequest = new GroupRequest();
        testGroupRequest.setUser(testUser);
        testGroupRequest.setGroup(testGroup);
        groupRequestRepository.save(testGroupRequest);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}").buildAndExpand(testGroup.getId()).toUriString();

        ResponseEntity<Void> response = restTemplate.exchange(urlWithParams, HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        boolean groupExists = groupRepository.existsById(testGroup.getId());
        assertThat(groupExists).isFalse();

        boolean groupMemberExists = groupMemberRepository.existsByUserIdAndGroupId(user.getId(), testGroup.getId());
        assertThat(groupMemberExists).isFalse();

        boolean groupRequestsExist = groupRequestRepository.existsByGroupId(testGroup.getId());
        assertThat(groupRequestsExist).isFalse();
    }

    @Order(3)
    @DisplayName("Successfully returning groups by name")
    @Test
    void successfullyReturnGroupsByName() {
        User testUser = new User();
        testUser.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser.setEmail("xanitev711@mcatag.com");
        testUser = userRepository.save(testUser);

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setAdmin(testUser);
        groupRepository.save(testGroup);

        String name = "Test";
        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).queryParam("name", name).toUriString();

        ResponseEntity<GroupDTO[]> response = restTemplate.getForEntity(urlWithParams, GroupDTO[].class);

        assertThat(response).isNotNull();
        assertThat(response.getBody().length).isEqualTo(1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        GroupDTO returnedGroup = response.getBody()[0];
        assertThat(returnedGroup).isNotNull();
        assertThat(returnedGroup.name()).isEqualTo(testGroup.getName());
        assertThat(returnedGroup.isPublic()).isEqualTo(testGroup.isPublic());
        assertThat(returnedGroup.adminEmail()).isEqualTo(testUser.getEmail());
    }

    @Order(4)
    @DisplayName("Successfully returning all requests for group")
    @Test
    void successfullyReturnAllGroupRequests() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser = new User();
        testUser.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser.setEmail("xanitev711@mcatag.com");
        testUser = userRepository.save(testUser);

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setAdmin(user);
        testGroup = groupRepository.save(testGroup);

        GroupRequest testGroupRequest = new GroupRequest();
        testGroupRequest.setUser(testUser);
        testGroupRequest.setGroup(testGroup);
        groupRequestRepository.save(testGroupRequest);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/requests").buildAndExpand(testGroup.getId()).toUriString();

        ResponseEntity<GroupRequestDTO[]> response = restTemplate.getForEntity(urlWithParams, GroupRequestDTO[].class);

        assertThat(response).isNotNull();
        assertThat(response.getBody().length).isEqualTo(1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        GroupRequestDTO returnedRequest = response.getBody()[0];
        assertThat(returnedRequest).isNotNull();
        assertThat(returnedRequest.userDTO()).isNotNull();
        assertThat(returnedRequest.userDTO().email()).isEqualTo(testUser.getEmail());
        assertThat(returnedRequest.userDTO()).isNotNull();
    }

    @Order(5)
    @DisplayName("Successfully accept request")
    @Test
    void successfullyAcceptRequest() throws ResourceNotFoundException {
        User user = jwtService.getUser();
        userRepository.save(user);

        User testUser = new User();
        testUser.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser.setEmail("xanitev711@mcatag.com");
        testUser = userRepository.save(testUser);

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setAdmin(user);
        testGroup = groupRepository.save(testGroup);

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(user);
        groupMember.setGroup(testGroup);
        groupMemberRepository.save(groupMember);

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setUser(testUser);
        groupRequest.setGroup(testGroup);
        groupRequest = groupRequestRepository.save(groupRequest);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{idGroup}/requests/{idRequest}/accept").buildAndExpand(testGroup.getId(), groupRequest.getId()).toUriString();

        ResponseEntity<Void> response = restTemplate.postForEntity(urlWithParams, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        boolean requestExists = groupRequestRepository.existsById(groupRequest.getId());
        assertThat(requestExists).isFalse();

        boolean groupExists = groupRepository.existsById(testGroup.getId());
        assertThat(groupExists).isTrue();

        boolean userIsMember = groupMemberRepository.existsByUserIdAndGroupId(testUser.getId(), testGroup.getId());
        assertThat(userIsMember).isTrue();
    }

    @Order(6)
    @DisplayName("Successfully reject request")
    @Test
    void successfullyRejectRequest() throws ResourceNotFoundException {

        User user = jwtService.getUser();
        userRepository.save(user);

        User testUser = new User();
        testUser.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser.setEmail("xanitev711@mcatag.com");
        testUser = userRepository.save(testUser);

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setAdmin(user);
        testGroup = groupRepository.save(testGroup);

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(user);
        groupMember.setGroup(testGroup);
        groupMemberRepository.save(groupMember);

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setUser(testUser);
        groupRequest.setGroup(testGroup);
        groupRequest = groupRequestRepository.save(groupRequest);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{idGroup}/requests/{idRequest}/reject").buildAndExpand(testGroup.getId(), groupRequest.getId()).toUriString();

        ResponseEntity<Void> response = restTemplate.postForEntity(urlWithParams, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        boolean requestExists = groupRequestRepository.existsById(groupRequest.getId());
        assertThat(requestExists).isFalse();

        boolean groupExists = groupRepository.existsById(testGroup.getId());
        assertThat(groupExists).isTrue();
    }

    @Order(7)
    @DisplayName("Successfully create request to join to private group")
    @Test
    void successfullyCreateRequestForPrivateGroup() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser = new User();
        testUser.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser.setEmail("xanitev711@mcatag.com");
        testUser = userRepository.save(testUser);

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setAdmin(testUser);
        testGroup = groupRepository.save(testGroup);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/join").buildAndExpand(testGroup.getId()).toUriString();

        ResponseEntity<ResolvedGroupRequestDTO> response = restTemplate.postForEntity(urlWithParams, null, ResolvedGroupRequestDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(response.getBody().user().id()).isEqualTo(user.getId());
        assertThat(response.getBody().group().idGroup()).isEqualTo(testGroup.getId());
        assertThat(response.getBody().status()).isEqualTo(ResolvedGroupRequestStatus.REQUEST_TO_JOIN_GROUP_CREATED);
        assertThat(response).isNotNull();

        boolean requestExists = groupRequestRepository.existsByGroupId(testGroup.getId());
        assertThat(requestExists).isTrue();

        boolean groupExists = groupRepository.existsById(testGroup.getId());
        assertThat(groupExists).isTrue();
    }

    @Order(8)
    @DisplayName("Successfully create request to join to public group and automatically accept")
    @Test
    void successfullyCreateRequestForPublicGroup() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser = new User();
        testUser.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser.setEmail("xanitev711@mcatag.com");
        testUser = userRepository.save(testUser);

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(true);
        testGroup.setAdmin(testUser);
        testGroup = groupRepository.save(testGroup);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/join").buildAndExpand(testGroup.getId()).toUriString();

        ResponseEntity<ResolvedGroupRequestDTO> response = restTemplate.postForEntity(urlWithParams, null, ResolvedGroupRequestDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(response.getBody().user().id()).isEqualTo(user.getId());
        assertThat(response.getBody().group().idGroup()).isEqualTo(testGroup.getId());
        assertThat(response.getBody().status()).isEqualTo(ResolvedGroupRequestStatus.REQUEST_TO_JOIN_GROUP_ACCEPTED);
        assertThat(response).isNotNull();

        boolean requestExists = groupRequestRepository.existsByGroupId(testGroup.getId());
        assertThat(requestExists).isFalse();

        boolean groupExists = groupRepository.existsById(testGroup.getId());
        assertThat(groupExists).isTrue();

        boolean groupMember = groupMemberRepository.existsByUserIdAndGroupId(user.getId(), testGroup.getId());
        assertThat(groupMember).isTrue();
    }

    @Order(9)
    @DisplayName("Successfully return all posts for group ")
    @Test
    void successfullyReturnPostsForGroup() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setAdmin(user);
        testGroup = groupRepository.save(testGroup);

        Post post = new Post();
        post.setGroup(testGroup);
        post.setPublic(true);
        post.setText("TEST");
        post.setOwner(user);
        post.setImgS3Key(null);
        post.setComments(null);
        postRepository.save(post);

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(user);
        groupMember.setGroup(testGroup);
        groupMemberRepository.save(groupMember);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/posts").buildAndExpand(testGroup.getId()).toUriString();

        ResponseEntity<PostDTO[]> response = restTemplate.getForEntity(urlWithParams, PostDTO[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getBody().length).isEqualTo(1);

        PostDTO returnedPost = response.getBody()[0];
        assertThat(returnedPost.id()).isEqualTo(post.getId());
        assertThat(returnedPost.text()).isEqualTo(post.getText());
        assertThat(returnedPost.userEmail()).isEqualTo(user.getEmail());
        assertThat(returnedPost.groupName()).isEqualTo(testGroup.getName());
    }


//    @Test
//    void check_if_creating_new_group_return_exception_that_group_with_that_name_already_exist() throws ResourceNotFoundException {
//        User user = jwtService.getUser();
//        userRepository.save(user);
//
//        Group testGroup = new Group();
//        testGroup.setName("Test");
//        testGroup.setPublic(false);
//        testGroup.setAdmin(user);
//
//        groupRepository.save(testGroup);
//
//        CreateGroupDTO createGroupDTO = new CreateGroupDTO("Test", false);
//
//        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL)
//                .toUriString();
//
//        ResponseEntity<GroupDTO> responseEntity = restTemplate.postForEntity(urlWithParams, createGroupDTO, GroupDTO.class);
//
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
//        assertThat(responseEntity.getBody()).withFailMessage("Group with that name already exists.");
//    }

//    @Order(1)
//    @DisplayName("Successfully creating group") //doradi
//    @Test
//    void successfullyCreatingGroup2() throws ResourceNotFoundException {
//        User user = jwtService.getUser();
//        userRepository.save(user);
//
//        CreateGroupDTO createGroupDTO = new CreateGroupDTO("Test", false);
//
//        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).toUriString();
//
//        GroupDTO response = restTemplate.postForObject(urlWithParams, createGroupDTO, GroupDTO.class);
//
//        assertThat(response.name()).isEqualTo(createGroupDTO.name());
//        assertThat(response.isPublic()).isEqualTo(createGroupDTO.isPublic());
//        assertThat(response).isNotNull();
//
//        ResponseEntity<ExceptionResponse> response1 = restTemplate.postForEntity(friendsApiURL + "/requests", new SentFriendRequestDTO("xanitev711@mcatag.com"), ExceptionResponse.class);
//        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response1.getBody()).isNotNull();
//        assertThat(response1.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_CREATING_FRIEND_REQUEST);
//        assertThat(response1.getBody().message()).isEqualTo("These users are already friends");
//    }

}
