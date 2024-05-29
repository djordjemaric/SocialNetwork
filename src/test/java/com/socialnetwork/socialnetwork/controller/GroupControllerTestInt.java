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
    @DisplayName("Creating group with duplicate name should return exception")
    @Test
    void creatingGroupWithDuplicateNameShouldReturnException() throws ResourceNotFoundException {
        User user = jwtService.getUser();
        userRepository.save(user);

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setAdmin(user);
        groupRepository.save(testGroup);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams, new CreateGroupDTO("Test", false), ExceptionResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_CREATING_GROUP);
        assertThat(response.getBody().message()).isEqualTo("Group with that name already exists.");
    }

    @Order(3)
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
    @Order(4)
    @DisplayName("Deleting group by non-admin user should return exception")
    @Test
    void deletingGroupByNonAdminUserShouldReturnException() throws ResourceNotFoundException {
        User user = jwtService.getUser();
        userRepository.save(user);

        User testUser = new User();
        testUser.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser.setEmail("xanitev711@mcatag.com");
        testUser = userRepository.save(testUser);

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setAdmin(testUser);
        testGroup = groupRepository.save(testGroup);

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(testUser);
        groupMember.setGroup(testGroup);
        groupMemberRepository.save(groupMember);

        GroupRequest testGroupRequest = new GroupRequest();
        testGroupRequest.setUser(user);
        testGroupRequest.setGroup(testGroup);
        groupRequestRepository.save(testGroupRequest);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}").buildAndExpand(testGroup.getId()).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.exchange(urlWithParams, HttpMethod.DELETE,null, ExceptionResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("You can't delete a group that you are not an admin of.");

    }

    @Order(5)
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

    @Order(6)
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

    @Order(7)
    @DisplayName("Returning all requests for non-existent group should return exception")
    @Test
    void returningAllRequestsForNonExistentGroupShouldReturnException() {
        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/requests").buildAndExpand(5).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.getForEntity(urlWithParams, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_GETTING_GROUP_REQUESTS);
        assertThat(response.getBody().message()).isEqualTo("Group with id " + 5 + "does not exist");
    }

    @Order(8)
    @DisplayName("Returning all requests for group when not admin should return exception")
    @Test
    void returningAllRequestsForGroupWhenNotAdminShouldReturnException() {
        User testUser = new User();
        testUser.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser.setEmail("xanitev711@mcatag.com");
        testUser = userRepository.save(testUser);

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setAdmin(testUser);
        testGroup = groupRepository.save(testGroup);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/requests").buildAndExpand(testGroup.getId()).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.getForEntity(urlWithParams, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("You are not an admin of a given group " + testGroup.getName());
    }

    @Order(9)
    @DisplayName("Successfully accept request")
    @Test
    void successfullyAcceptRequest() throws ResourceNotFoundException {
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

    @Order(10)
    @DisplayName("Accepting request for non-existent group should return exception")
    @Test
    void acceptingRequestForNonExistentGroupShouldReturnException() {
        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{idGroup}/requests/{idRequest}/accept").buildAndExpand(5,5).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams,null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_MANAGING_GROUP_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("Group with id " + 5 + "does not exist");
    }

    @Order(11)
    @DisplayName("Accepting request for non-existent group request should return exception")
    @Test
    void acceptingRequestForNonExistentGroupRequestShouldReturnException() throws ResourceNotFoundException {
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

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(user);
        groupMember.setGroup(testGroup);
        groupMemberRepository.save(groupMember);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{idGroup}/requests/{idRequest}/accept").buildAndExpand(testGroup.getId(),5).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams,null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_MANAGING_GROUP_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("Group request with id " + 5 + "does not exist");
    }

//    @Order(12)
//    @DisplayName("Accepting request for non-existent user should return exception")
//    @Test
//    void acceptingRequestForNonExistentUserShouldReturnException() throws ResourceNotFoundException {
//        User user = jwtService.getUser();
//
//        User testUser = new User();
//        testUser.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
//        testUser.setEmail("xanitev711@mcatag.com");
//
//
//        Group testGroup = new Group();
//        testGroup.setName("Test");
//        testGroup.setPublic(false);
//        testGroup.setAdmin(user);
//        testGroup = groupRepository.save(testGroup);
//
//        GroupMember groupMember = new GroupMember();
//        groupMember.setMember(user);
//        groupMember.setGroup(testGroup);
//        groupMemberRepository.save(groupMember);
//
//        GroupRequest groupRequest = new GroupRequest();
//        groupRequest.setUser(testUser);
//        groupRequest.setGroup(testGroup);
//        groupRequest = groupRequestRepository.save(groupRequest);
//
//        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{idGroup}/requests/{idRequest}/accept").buildAndExpand(testGroup.getId(),groupRequest.getId()).toUriString();
//
//        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams,null, ExceptionResponse.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_MANAGING_GROUP_REQUEST);
//        assertThat(response.getBody().message()).isEqualTo("User with id " + testUser.getId() + "does not exist");
//    }


    @Order(12)
    @DisplayName("Accepting request by non-admin user should return exception")
    @Test
    void acceptingRequestByNonAdminUserShouldReturnException() {
        User testUser = new User();
        testUser.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser.setEmail("xanitev711@mcatag.com");
        testUser = userRepository.save(testUser);

        User testUser2 = new User();
        testUser2.setUserSub("73140822-2011-705f-ce8c-675fa425e435");
        testUser2.setEmail("mapsesisto@gufum.com");
        testUser2 = userRepository.save(testUser2);

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setAdmin(testUser);
        testGroup = groupRepository.save(testGroup);

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(testUser);
        groupMember.setGroup(testGroup);
        groupMemberRepository.save(groupMember);

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setUser(testUser2);
        groupRequest.setGroup(testGroup);
        groupRequest = groupRequestRepository.save(groupRequest);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{idGroup}/requests/{idRequest}/accept").buildAndExpand(testGroup.getId(),groupRequest.getId()).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams,null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("You are not an admin of a given group " + testGroup.getName());
    }


//    @Order(14) OVO NE VALJA SREDI TO
//    @DisplayName("Successfully accept request")
//    @Test
//    void successfullyAcceptRequest4() throws ResourceNotFoundException {
//        User user = jwtService.getUser();
//
//        User testUser = new User();
//        testUser.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
//        testUser.setEmail("xanitev711@mcatag.com");
//        testUser =  userRepository.save(testUser);
//
//
//        User testUser2 = new User();
//        testUser2.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
//        testUser2.setEmail("xanitev711@mcatag.com");
//        testUser2 =  userRepository.save(testUser2);
//
//        Group testGroup = new Group();
//        testGroup.setName("Test");
//        testGroup.setPublic(false);
//        testGroup.setAdmin(user);
//        testGroup = groupRepository.save(testGroup);
//
//        GroupMember groupMember = new GroupMember();
//        groupMember.setMember(user);
//        groupMember.setGroup(testGroup);
//        groupMemberRepository.save(groupMember);
//
//        GroupRequest groupRequest = new GroupRequest();
//        groupRequest.setUser(testUser2);
//        groupRequest.setGroup(testGroup);
//        groupRequest = groupRequestRepository.save(groupRequest);
//
//
//        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{idGroup}/requests/{idRequest}/accept").buildAndExpand(testGroup.getId(),groupRequest.getId()).toUriString();
//
//        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams,null, ExceptionResponse.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_MANAGING_GROUP_REQUEST);
//        assertThat(response.getBody().message()).isEqualTo("You are not an admin of a given group " + testGroup.getName());
//    }

    @Order(13)
    @DisplayName("Attempting to accept request for public group returns exception")
    @Test
    void acceptingRequestForPublicGroupShouldReturnException() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser = new User();
        testUser.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser.setEmail("xanitev711@mcatag.com");
        testUser =  userRepository.save(testUser);

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(true);
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


        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{idGroup}/requests/{idRequest}/accept").buildAndExpand(testGroup.getId(),groupRequest.getId()).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams,null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_MANAGING_GROUP_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("You can't accept or reject a request if group is public");
    }

    @Order(14)
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

    @Order(15)
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

    @Order(16)
    @DisplayName("Attempting to create request to join non-existing private group returns exception")
    @Test
    void createRequestForNonExistingPrivateGroupShouldReturnException() {
        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/join").buildAndExpand(5).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams,null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_CREATING_REQUEST_GROUP);
        assertThat(response.getBody().message()).isEqualTo("Group with id " + 5 + "does not exist");

    }

    @Order(17)
    @DisplayName("Creating duplicate request to join private group returns exception")
    @Test
    void createDuplicateRequestForPrivateGroupShouldReturnException() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setAdmin(user);
        testGroup = groupRepository.save(testGroup);

        GroupRequest request = new GroupRequest();
        request.setUser(user);
        request.setGroup(testGroup);
        groupRequestRepository.save(request);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/join").buildAndExpand(testGroup.getId()).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams,null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_CREATING_REQUEST_GROUP);
        assertThat(response.getBody().message()).isEqualTo("The request has already been sent.");

    }

    @Order(18)
    @DisplayName("Create request to join private group when already a member returns exception")
    @Test
    void createRequestForPrivateGroupWhenAlreadyMemberShouldReturnException() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setAdmin(user);
        testGroup = groupRepository.save(testGroup);

        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(testGroup);
        groupMember.setMember(user);
        groupMemberRepository.save(groupMember);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/join").buildAndExpand(testGroup.getId()).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(urlWithParams,null, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_CREATING_REQUEST_GROUP);
        assertThat(response.getBody().message()).isEqualTo("You are already member of that group.");

    }
    @Order(19)
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

    @Order(20)
    @DisplayName("Successfully return all posts for private group ")
    @Test
    void successfullyReturnPostsForPrivateGroup() throws ResourceNotFoundException {
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


    @Order(21)
    @DisplayName("Successfully return all posts for public group ")
    @Test
    void successfullyReturnPostsForPublicGroup() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(true);
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

    @Order(22)
    @DisplayName("Successfully return all posts for public group in which user is not a member")
    @Test
    void successfullyReturnPostsForPublicGroupAndUserIsNotAMemberOfThatGroup() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(true);
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

    @Order(23)
    @DisplayName("Return all posts for which group which does not exist return exception ")
    @Test
    void returnAllPostsForGroupThatDoesNotExistReturnException() {
        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL).path("/{id}/posts").buildAndExpand(5).toUriString();

        ResponseEntity<ExceptionResponse> response = restTemplate.getForEntity(urlWithParams, ExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_GETTING_GROUP_POSTS);
        assertThat(response.getBody().message()).isEqualTo("Group with id " + 5 + "does not exist");
    }

    @Order(24)
    @DisplayName("Return all posts for group if user is not a member of that group return exception")
    @Test
    void returnAllPostsForGroupInWhichUserIsNotAMemberOfReturnException() throws ResourceNotFoundException {
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

        Post post = new Post();
        post.setGroup(testGroup);
        post.setPublic(true);
        post.setText("TEST");
        post.setOwner(testUser);
        post.setImgS3Key(null);
        post.setComments(null);
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


}
