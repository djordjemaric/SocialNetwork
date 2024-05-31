package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.IntegrationTestConfiguration;
import com.socialnetwork.socialnetwork.dto.group.*;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.entity.*;
import com.socialnetwork.socialnetwork.exceptions.ErrorCode;
import com.socialnetwork.socialnetwork.exceptions.ExceptionResponse;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("Successfully creating group")
    @Test
    void testCreateGroup() {
        CreateGroupDTO createGroupDTO = new CreateGroupDTO("Test", false);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).toUriString();

        ResponseEntity<GroupDTO> response = restTemplate.postForEntity(urlWithParams, createGroupDTO, GroupDTO.class);

        assertThat(response.getBody().name()).isEqualTo(createGroupDTO.name());
        assertThat(response.getBody().isPublic()).isEqualTo(createGroupDTO.isPublic());
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @DisplayName("Creating group with duplicate name should return exception")
    @Test
    void testCreatingGroupAlreadyExist() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        Group testGroup = getTestGroup(user, false);
        groupRepository.save(testGroup);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams, new CreateGroupDTO("Test", false), ExceptionResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_CREATING_GROUP);
        assertThat(response.getBody().message()).isEqualTo("Group with that name already exists.");
    }

    @DisplayName("Successfully deleting group")
    @Test
    void testDeleteGroup() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser = getTestUser();
        userRepository.save(testUser);

        Group testGroup = getTestGroup(user, false);
        groupRepository.save(testGroup);

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

        Assertions.assertFalse(groupRepository.existsById(testGroup.getId()));

        boolean groupMemberExists = groupMemberRepository.existsByUserIdAndGroupId(user.getId(), testGroup.getId());
        assertThat(groupMemberExists).isFalse();

        boolean groupRequestsExist = groupRequestRepository.existsByGroupId(testGroup.getId());
        assertThat(groupRequestsExist).isFalse();
    }

    @DisplayName("Deleting group by non-admin user should return exception")
    @Test
    void testDeleteGroupByNoAdminUser() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser = getTestUser();
        userRepository.save(testUser);

        Group testGroup = getTestGroup(testUser, false);
        groupRepository.save(testGroup);

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(testUser);
        groupMember.setGroup(testGroup);
        groupMemberRepository.save(groupMember);

        GroupRequest testGroupRequest = new GroupRequest();
        testGroupRequest.setUser(user);
        testGroupRequest.setGroup(testGroup);
        groupRequestRepository.save(testGroupRequest);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}").buildAndExpand(testGroup.getId()).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.exchange(urlWithParams, HttpMethod.DELETE, null, ExceptionResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("You can't delete a group that you are not an admin of.");

    }

    @DisplayName("Successfully returning groups by name")
    @Test
    void testGetGroups() {
        User testUser = getTestUser();
        userRepository.save(testUser);

        Group testGroup = getTestGroup(testUser, false);
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

    @DisplayName("Successfully returning all requests for group")
    @Test
    void testGetRequests() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser = getTestUser();
        userRepository.save(testUser);

        Group testGroup = getTestGroup(user, false);
        groupRepository.save(testGroup);

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

    @DisplayName("Returning all requests for non-existent group should return exception")
    @Test
    void testGetRequestsNoExistGroup() {
        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/requests").buildAndExpand(5).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.getForEntity(urlWithParams, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_GETTING_GROUP_REQUESTS);
        assertThat(response.getBody().message()).isEqualTo("Group with id " + 5 + " does not exist.");
    }

    @DisplayName("Returning all requests for group when not admin should return exception")
    @Test
    void testGetRequestsNoAdmin() {
        User testUser = getTestUser();
        userRepository.save(testUser);

        Group testGroup = getTestGroup(testUser, false);
        groupRepository.save(testGroup);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/requests").buildAndExpand(testGroup.getId()).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.getForEntity(urlWithParams, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("You are not an admin of a given group " + testGroup.getName());
    }

    @DisplayName("Successfully accept request")
    @Test
    void testAcceptRequest() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser = getTestUser();
        userRepository.save(testUser);

        Group testGroup = getTestGroup(user, false);
        groupRepository.save(testGroup);

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

    @DisplayName("Accepting request for non-existent group should return exception")
    @Test
    void testAcceptRequestNoExistGroup() {
        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{idGroup}/requests/{idRequest}/accept").buildAndExpand(5, 5).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams, null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_MANAGING_GROUP_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("Group with id " + 5 + " does not exist.");
    }

    @DisplayName("Accepting request for non-existent group request should return exception")
    @Test
    void testAcceptRequestNoExistGroupRequest() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser = getTestUser();
        userRepository.save(testUser);

        Group testGroup = getTestGroup(user, false);
        groupRepository.save(testGroup);

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(user);
        groupMember.setGroup(testGroup);
        groupMemberRepository.save(groupMember);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{idGroup}/requests/{idRequest}/accept").buildAndExpand(testGroup.getId(), 5).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams, null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_MANAGING_GROUP_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("Group request with id " + +5 + " and group id " + testGroup.getId() + " does not exist.");
    }

    @DisplayName("Accepting request for non-existent group request for given group id and given request id should return exception")
    @Test
    void testAcceptRequestNoExistGroupAndRequest() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser = getTestUser();
        userRepository.save(testUser);

        Group testGroup = getTestGroup(user, false);
        groupRepository.save(testGroup);

        Group testGroup2 = getTestGroup2(user);
        groupRepository.save(testGroup2);

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(user);
        groupMember.setGroup(testGroup);
        groupMemberRepository.save(groupMember);

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setUser(testUser);
        groupRequest.setGroup(testGroup2);
        groupRequest = groupRequestRepository.save(groupRequest);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{idGroup}/requests/{idRequest}/accept").buildAndExpand(testGroup.getId(), groupRequest.getId()).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams, null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_MANAGING_GROUP_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("Group request with id " + +groupRequest.getId() + " and group id " + testGroup.getId() + " does not exist.");
    }


    @DisplayName("Accepting request by non-admin user should return exception")
    @Test
    void testAcceptRequestNoExistAdmin() {
        User testUser = getTestUser();
        userRepository.save(testUser);

        User testUser2 = getTestUser2();
        userRepository.save(testUser2);

        Group testGroup = getTestGroup(testUser, false);
        groupRepository.save(testGroup);

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(testUser);
        groupMember.setGroup(testGroup);
        groupMemberRepository.save(groupMember);

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setUser(testUser2);
        groupRequest.setGroup(testGroup);
        groupRequest = groupRequestRepository.save(groupRequest);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{idGroup}/requests/{idRequest}/accept").buildAndExpand(testGroup.getId(), groupRequest.getId()).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams, null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("You are not an admin of a given group " + testGroup.getName());
    }

    @DisplayName("Attempting to accept request for public group returns exception")
    @Test
    void testAcceptRequestPublicGroup() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser = getTestUser();
        userRepository.save(testUser);

        Group testGroup = getTestGroup(user, true);
        groupRepository.save(testGroup);

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(user);
        groupMember.setGroup(testGroup);
        groupMemberRepository.save(groupMember);

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setUser(testUser);
        groupRequest.setGroup(testGroup);
        groupRequest = groupRequestRepository.save(groupRequest);


        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{idGroup}/requests/{idRequest}/accept").buildAndExpand(testGroup.getId(), groupRequest.getId()).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams, null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_MANAGING_GROUP_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("You can't accept or reject a request if group is public");
    }

    @DisplayName("Successfully reject request")
    @Test
    void testRejectRequest() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser = getTestUser();
        userRepository.save(testUser);

        Group testGroup = getTestGroup(user, false);
        groupRepository.save(testGroup);

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

    @DisplayName("Successfully create request to join to private group")
    @Test
    void testCreateRequestPublicGroup() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser = getTestUser();
        userRepository.save(testUser);

        Group testGroup = getTestGroup(testUser, false);
        groupRepository.save(testGroup);

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

    @DisplayName("Attempting to create request to join non-existing private group returns exception")
    @Test
    void testCreateRequestNoExistGroup() {
        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/join").buildAndExpand(5).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams, null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_CREATING_REQUEST_GROUP);
        assertThat(response.getBody().message()).isEqualTo("Group with id " + 5 + " does not exist.");

    }

    @DisplayName("Creating duplicate request to join private group returns exception")
    @Test
    void testCreateRequestAlreadyExist() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        Group testGroup = getTestGroup(user, false);
        groupRepository.save(testGroup);

        GroupRequest request = new GroupRequest();
        request.setUser(user);
        request.setGroup(testGroup);
        groupRequestRepository.save(request);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/join").buildAndExpand(testGroup.getId()).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams, null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_CREATING_REQUEST_GROUP);
        assertThat(response.getBody().message()).isEqualTo("The request has already been sent.");

    }

    @DisplayName("Create request to join private group when already a member returns exception")
    @Test
    void testCreateRequestAlreadyMember() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        Group testGroup = getTestGroup(user, false);
        groupRepository.save(testGroup);

        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(testGroup);
        groupMember.setMember(user);
        groupMemberRepository.save(groupMember);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/join").buildAndExpand(testGroup.getId()).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams, null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_CREATING_REQUEST_GROUP);
        assertThat(response.getBody().message()).isEqualTo("You are already member of that group.");

    }

    @DisplayName("Successfully create request to join to public group and automatically accept")
    @Test
    void testCreateRequestPrivateGroup() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser = getTestUser();
        userRepository.save(testUser);

        Group testGroup = getTestGroup(testUser, true);
        groupRepository.save(testGroup);

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

    @DisplayName("Successfully return all posts for private group ")
    @Test
    void testGetPostsPrivateGroup() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        Group testGroup = getTestGroup(user, false);
        groupRepository.save(testGroup);

        Post post = getTestPost(testGroup, user);
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


    @DisplayName("Successfully return all posts for public group ")
    @Test
    void testGetPostsPublicGroup() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        Group testGroup = getTestGroup(user, false);
        groupRepository.save(testGroup);

        Post post = getTestPost(testGroup, user);
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

    @DisplayName("Successfully return all posts for public group in which user is not a member")
    @Test
    void testGetPostsNotAPublicGroupMember() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        Group testGroup = getTestGroup(user, true);
        groupRepository.save(testGroup);

        Post post = getTestPost(testGroup, user);
        postRepository.save(post);

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

    @DisplayName("Return all posts for which group which does not exist return exception ")
    @Test
    void testGetPostsNoExistGroup() {
        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/posts").buildAndExpand(5).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.getForEntity(urlWithParams, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_GETTING_GROUP_POSTS);
        assertThat(response.getBody().message()).isEqualTo("Group with id " + 5 + " does not exist.");
    }

    @DisplayName("Return all posts for group if user is not a member of that group return exception")
    @Test
    void testGetPostsNotAPrivateGroupMember() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser = getTestUser();
        userRepository.save(testUser);

        Group testGroup = getTestGroup(testUser, false);
        groupRepository.save(testGroup);

        Post post = getTestPost(testGroup, testUser);
        postRepository.save(post);

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(testUser);
        groupMember.setGroup(testGroup);
        groupMemberRepository.save(groupMember);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/posts").buildAndExpand(testGroup.getId()).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.getForEntity(urlWithParams, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("User " + user.getEmail() + " is not a member of the group with id: " + testGroup.getId());
    }

    private Post getTestPost(Group group, User owner) {
        Post post = new Post();
        post.setGroup(group);
        post.setPublic(true);
        post.setText("TEST");
        post.setOwner(owner);
        post.setImgS3Key(null);
        post.setComments(null);
        return post;
    }

    private User getTestUser() {
        User testUser = new User();
        testUser.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser.setEmail("xanitev711@mcatag.com");
        return testUser;
    }

    private User getTestUser2() {
        User testUser = new User();
        testUser.setUserSub("73140822-2011-705f-ce8c-675fa425e435");
        testUser.setEmail("mapsesisto@gufum.com");
        return testUser;
    }

    private Group getTestGroup(User admin, boolean isPublic) {
        Group group = new Group();
        group.setName("Test");
        group.setPublic(isPublic);
        group.setAdmin(admin);

        return group;
    }

    private Group getTestGroup2(User admin) {
        Group group = new Group();
        group.setName("Test");
        group.setPublic(false);
        group.setAdmin(admin);

        return group;
    }

}
