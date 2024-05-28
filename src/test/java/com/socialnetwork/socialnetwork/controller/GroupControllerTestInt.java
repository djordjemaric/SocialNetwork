package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.IntegrationTestConfiguration;
import com.socialnetwork.socialnetwork.dto.friendRequest.FriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupRequestDTO;
import com.socialnetwork.socialnetwork.entity.FriendRequest;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.repository.FriendRequestRepository;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GroupControllerTestInt extends IntegrationTestConfiguration {

    private String groupsApiURL = "/api/groups";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private GroupRepository groupRepository;

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
        testGroup.setPosts(null);
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
    void getAllRequestForGroup() throws ResourceNotFoundException {
        User testUser1 = new User();
        testUser1.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser1.setEmail("xanitev711@mcatag.com");
        testUser1 = userRepository.save(testUser1);



        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setPosts(null);
        testGroup.setAdmin(testUser1);
        groupRepository.save(testGroup);

        GroupRequest testGroupRequest =  new GroupRequest();
        testGroupRequest.setUser(testUser1);
        testGroupRequest.setGroup(testGroup);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL)
                .path("/{id}/requests")
                .buildAndExpand(testGroup.getId())
                .toUriString();

        GroupRequestDTO[] grRequestsResponseArray = restTemplate.getForObject(urlWithParams, GroupRequestDTO[].class);

        assertThat(grRequestsResponseArray.length).isEqualTo(1);
    }

    @Test
    void createGroup() throws ResourceNotFoundException {
        User testUser1 = new User();
        testUser1.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser1.setEmail("xanitev711@mcatag.com");
        testUser1 = userRepository.save(testUser1);

        CreateGroupDTO createGroupDTO = new CreateGroupDTO("Test",false);


        Group testGroup = new Group();
        testGroup.setName("Test");
        testGroup.setPublic(false);
        testGroup.setPosts(null);
        testGroup.setAdmin(testUser1);
        groupRepository.save(testGroup);

        GroupRequest testGroupRequest =  new GroupRequest();
        testGroupRequest.setUser(testUser1);
        testGroupRequest.setGroup(testGroup);

        String urlWithParams = UriComponentsBuilder.fromUriString(groupsApiURL)
                .path("/")
                .buildAndExpand(testGroup.getId())
                .toUriString();

        GroupRequestDTO[] grRequestsResponseArray = restTemplate.postForObject(urlWithParams,createGroupDTO,GroupRequestDTO[].class);

        assertThat(grRequestsResponseArray.length).isEqualTo(1);
    }
}
