package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.IntegrationTestConfiguration;
import com.socialnetwork.socialnetwork.dto.friendRequest.FriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupRequestDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestDTO;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.entity.*;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class GroupControllerTestInt extends IntegrationTestConfiguration {

    private String groupsApiURL = "/api/groups";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private GroupRequestRepository groupRequestRepository;

    @Autowired
    private GroupMapper groupMapper;

    @Test
    void testFriendsConnection() throws ResourceNotFoundException {
        User testUser1 = new User();
        testUser1.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser1.setEmail("xanitev711@mcatag.com");
        testUser1 = userRepository.save(testUser1);


        User currentTestUser = jwtService.getUser();
        FriendRequest testFriendRequest = new FriendRequest();
        testFriendRequest.setFrom(testUser1);
        testFriendRequest.setTo(currentTestUser);
        friendRequestRepository.save(testFriendRequest);

        FriendRequestDTO[] frResponseArray = restTemplate.getForObject(groupsApiURL + "/requests", FriendRequestDTO[].class);

        assertThat(frResponseArray.length).isEqualTo(1);
        assertThat(frResponseArray[0].requestSender()).isEqualTo(testUser1.getEmail());
    }

    @Test
    void check_if_get_groups_by_name_return_groups_by_given_name() throws ResourceNotFoundException {
        User testUser1 = new User();
        testUser1.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser1.setEmail("xanitev711@mcatag.com");
        testUser1 = userRepository.save(testUser1);

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
//        testGroup.setPosts(null);
        testGroup.setAdmin(testUser1);
        groupRepository.save(testGroup);

        String name = "Test";
        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL)
                .queryParam("name", name)
                .toUriString();

        GroupDTO[] grResponseArray = restTemplate.getForObject(urlWithParams, GroupDTO[].class);

        assertThat(grResponseArray.length).isEqualTo(1);
    }


    @Test
    void getAllRequestForGroup_success() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser1 = new User();
        testUser1.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser1.setEmail("xanitev711@mcatag.com");
        testUser1 = userRepository.save(testUser1);

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
//        testGroup.setPosts(new ArrayList<>());
        testGroup.setAdmin(user);
        testGroup = groupRepository.save(testGroup);

        GroupRequest testGroupRequest =  new GroupRequest();
        testGroupRequest.setUser(testUser1);
        testGroupRequest.setGroup(testGroup);
        groupRequestRepository.save(testGroupRequest);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL)
                .path("/{id}/requests")
                .buildAndExpand(testGroup.getId())
                .toUriString();

        GroupRequestDTO[] grRequestsResponseArray = restTemplate.getForObject(urlWithParams, GroupRequestDTO[].class);

        assertThat(grRequestsResponseArray.length).isEqualTo(1);
    }

    @Test
    void getAllPostsByGroupId_success() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setAdmin(user);
//        testGroup.setPosts(null);
        testGroup = groupRepository.save(testGroup);


        Post post = new Post();
        post.setGroup(testGroup);
        post.setPublic(true);
        post.setText("TEST");
        post.setOwner(user);
        post.setImgS3Key(null);
        post.setComments(null);
        postRepository.save(post);
//        testGroup.setPosts(List.of(post));

        GroupMember groupMember = new GroupMember();
        groupMember.setMember(user);
        groupMember.setGroup(testGroup);
        groupMemberRepository.save(groupMember);



        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL)
                .path("/{id}/posts")
                .buildAndExpand(testGroup.getId())
                .toUriString();

        PostDTO[] grRequestsResponseArray = restTemplate.getForObject(urlWithParams, PostDTO[].class);

        assertThat(grRequestsResponseArray.length).isEqualTo(1);
    }
    @Test
    void createRequestToJoinGroup_success() throws ResourceNotFoundException {
        User user = jwtService.getUser();

        User testUser1 = new User();
        testUser1.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser1.setEmail("xanitev711@mcatag.com");
        testUser1 = userRepository.save(testUser1);


        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
//        testGroup.setPosts(null);
        testGroup.setAdmin(testUser1);
        testGroup = groupRepository.save(testGroup);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL)
                .path("/{id}/join")
                .buildAndExpand(testGroup.getId())
                .toUriString();

        ResolvedGroupRequestDTO grRequestsResponseArray = restTemplate.postForObject(urlWithParams,null, ResolvedGroupRequestDTO.class);

        assertThat(grRequestsResponseArray.user().id()).isEqualTo(user.getId());
        assertThat(grRequestsResponseArray.group().idGroup()).isEqualTo(testGroup.getId());
        assertThat(grRequestsResponseArray).isNotNull();
    }

    @Test
    void check_if_creating_new_group_return_succesufull_created_group() throws ResourceNotFoundException {
        User user = jwtService.getUser();
        userRepository.save(user);

        CreateGroupDTO createGroupDTO = new CreateGroupDTO("Test", false);

        Group group = groupMapper.dtoToEntity(user,createGroupDTO);

        GroupDTO groupDTO = groupMapper.entityToGroupDto(group);

        assertThat(groupDTO.name()).isEqualTo(createGroupDTO.name());
        assertThat(groupDTO.isPublic()).isEqualTo(createGroupDTO.isPublic());
        assertThat(groupDTO).isNotNull();

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL)
                .toUriString();

        GroupDTO grResponse = restTemplate.postForObject(urlWithParams,createGroupDTO, GroupDTO.class);

        assertThat(grResponse.name()).isEqualTo(createGroupDTO.name());
        assertThat(grResponse.isPublic()).isEqualTo(createGroupDTO.isPublic());
        assertThat(grResponse).isNotNull();
    }

    @AfterEach
    void cleanupDatabase(){
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void check_if_creating_new_group_return_exception_that_group_with_that_name_already_exist() throws ResourceNotFoundException {
        User user = jwtService.getUser();
        userRepository.save(user);

        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setAdmin(user);

        groupRepository.save(testGroup);

        CreateGroupDTO createGroupDTO = new CreateGroupDTO("Test", false);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL)
                .toUriString();

        ResponseEntity<GroupDTO> responseEntity = restTemplate.postForEntity(urlWithParams, createGroupDTO, GroupDTO.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(responseEntity.getBody()).withFailMessage("Group with that name already exists.");
    }


}
