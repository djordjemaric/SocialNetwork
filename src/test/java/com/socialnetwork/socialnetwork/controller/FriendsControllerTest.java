package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.IntegrationTestConfiguration;
import com.socialnetwork.socialnetwork.SocialNetworkApplication;
import com.socialnetwork.socialnetwork.dto.friendRequest.FriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.PreviewFriendRequestDTO;
import com.socialnetwork.socialnetwork.dto.friendRequest.SentFriendRequestDTO;
import com.socialnetwork.socialnetwork.entity.FriendRequest;
import com.socialnetwork.socialnetwork.entity.Friends;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.ErrorCode;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.repository.FriendRequestRepository;
import com.socialnetwork.socialnetwork.repository.FriendsRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;



class FriendsControllerTest extends IntegrationTestConfiguration {

    private String friendsApiURL = "/api/friends";

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private FriendsRepository friendsRepository;

    @AfterEach
    void cleanDatabase() throws ResourceNotFoundException {
        friendRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Testing if there are no pending requests")
    @Order(2)
    void userFriendsRequestsShouldNotShow() throws ResourceNotFoundException {

        FriendRequestDTO[] frResponseArray = restTemplate.getForObject(friendsApiURL + "/requests", FriendRequestDTO[].class);

        assertThat(frResponseArray.length).isEqualTo(0);
    }

    @Test
    @DisplayName("Testing if user can see his pending friend requests")
    @Order(1)
    void userFriendsRequestsShouldShow() throws ResourceNotFoundException {

        User currentTestUser = jwtService.getUser();

        User testUser1 = new User();
        testUser1.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser1.setEmail("xanitev711@mcatag.com");
        userRepository.save(testUser1);

        FriendRequest testFriendRequest1 = new FriendRequest();
        testFriendRequest1.setFrom(testUser1);
        testFriendRequest1.setTo(currentTestUser);
        friendRequestRepository.save(testFriendRequest1);

        User testUser2 = new User();
        testUser2.setUserSub("73140822-2011-705f-ce8c-675fa425e435");
        testUser2.setEmail("mapsesisto@gufum.com");
        userRepository.save(testUser2);

        FriendRequest testFriendRequest2 = new FriendRequest();
        testFriendRequest2.setFrom(testUser2);
        testFriendRequest2.setTo(currentTestUser);
        friendRequestRepository.save(testFriendRequest2);

        FriendRequestDTO[] frResponseArray = restTemplate.getForObject(friendsApiURL + "/requests", FriendRequestDTO[].class);

        assertThat(frResponseArray.length).isEqualTo(2);
        assertThat(frResponseArray[0].requestSender()).isEqualTo(testUser1.getEmail());
        assertThat(frResponseArray[1].requestSender()).isEqualTo(testUser2.getEmail());
    }

    @Test
    @DisplayName("Testing if user can send friend requests")
    @Order(3)
    void userShouldSendRequests() throws ResourceNotFoundException {
        User currentTestUser = jwtService.getUser();

        User testUser1 = new User();
        testUser1.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser1.setEmail("xanitev711@mcatag.com");
        userRepository.save(testUser1);

        User testUser2 = new User();
        testUser2.setUserSub("73140822-2011-705f-ce8c-675fa425e435");
        testUser2.setEmail("mapsesisto@gufum.com");
        userRepository.save(testUser2);

        ResponseEntity<PreviewFriendRequestDTO> firstRequestResponse = restTemplate.postForEntity(friendsApiURL + "/requests", new SentFriendRequestDTO(testUser1.getEmail()), PreviewFriendRequestDTO.class);
        assertThat(firstRequestResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(firstRequestResponse.getBody().sender()).isEqualTo(currentTestUser.getEmail());
        assertThat(firstRequestResponse.getBody().receiver()).isEqualTo(testUser1.getEmail());

        ResponseEntity<PreviewFriendRequestDTO> secondRequestResponse = restTemplate.postForEntity(friendsApiURL + "/requests", new SentFriendRequestDTO(testUser2.getEmail()), PreviewFriendRequestDTO.class);
        assertThat(secondRequestResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(secondRequestResponse.getBody().sender()).isEqualTo(currentTestUser.getEmail());
        assertThat(secondRequestResponse.getBody().receiver()).isEqualTo(testUser2.getEmail());

        List<FriendRequest> friendRequests = friendRequestRepository.findAll();
        assertThat(friendRequests.get(0).getTo().getEmail()).isEqualTo(testUser1.getEmail());
        assertThat(friendRequests.get(0).getFrom().getEmail()).isEqualTo(currentTestUser.getEmail());
        assertThat(friendRequests.get(1).getTo().getEmail()).isEqualTo(testUser2.getEmail());
        assertThat(friendRequests.get(1).getFrom().getEmail()).isEqualTo(currentTestUser.getEmail());

    }

//    @Test
//    @DisplayName("Sending a request to a non existing user")
//    @Order(4)
//    void userSendingToANonExistingFriend() throws ResourceNotFoundException {
//
//        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(friendsApiURL + "/requests", new SentFriendRequestDTO("anonymous@test.com"), ExceptionResponse.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_CREATING_FRIEND_REQUEST);
//        assertThat(response.getBody().message()).isEqualTo("User not found. Wrong email sent");
//    }

//    @Test
//    @DisplayName("Sending a friend request to yourself")
//    @Order(5)
//    void userSendingToHimself() throws ResourceNotFoundException {
//
//        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(friendsApiURL + "/requests", new SentFriendRequestDTO("vica.ristic@gmail.com"), ExceptionResponse.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_CREATING_FRIEND_REQUEST);
//        assertThat(response.getBody().message()).isEqualTo("Can't send request to yourself");
//    }

//    @Test
//    @DisplayName("Checking users are already friends")
//    @Order(6)
//    void usersAreAlreadyFriendsBothWays() throws ResourceNotFoundException {
//
//        User currentUser = jwtService.getUser();
//
//        User testUser1 = new User();
//        testUser1.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
//        testUser1.setEmail("xanitev711@mcatag.com");
//        userRepository.save(testUser1);
//
//        User testUser2 = new User();
//        testUser2.setUserSub("73140822-2011-705f-ce8c-675fa425e435");
//        testUser2.setEmail("mapsesisto@gufum.com");
//        userRepository.save(testUser2);
//
//        Friends friends1 = new Friends();
//        friends1.setFriend(currentUser);
//        friends1.setFriendTo(testUser1);
//        friendsRepository.save(friends1);
//
//        Friends friends2 = new Friends();
//        friends2.setFriend(testUser2);
//        friends2.setFriendTo(currentUser);
//        friendsRepository.save(friends2);
//
//        ResponseEntity<ExceptionResponse> response1 = restTemplate.postForEntity(friendsApiURL + "/requests", new SentFriendRequestDTO("xanitev711@mcatag.com"), ExceptionResponse.class);
//        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response1.getBody()).isNotNull();
//        assertThat(response1.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_CREATING_FRIEND_REQUEST);
//        assertThat(response1.getBody().message()).isEqualTo("These users are already friends");
//
//        ResponseEntity<ExceptionResponse> response2 = restTemplate.postForEntity(friendsApiURL + "/requests", new SentFriendRequestDTO("mapsesisto@gufum.com"), ExceptionResponse.class);
//        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response2.getBody()).isNotNull();
//        assertThat(response2.getBody().errorCode()).isEqualTo(ErrorCode.ERROR_CREATING_FRIEND_REQUEST);
//        assertThat(response2.getBody().message()).isEqualTo("These users are already friends");
//    }
}