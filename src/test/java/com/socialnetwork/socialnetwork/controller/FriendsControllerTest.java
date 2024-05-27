package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.IntegrationTestConfiguration;
import com.socialnetwork.socialnetwork.SocialNetworkApplication;
import com.socialnetwork.socialnetwork.dto.friendRequest.FriendRequestDTO;
import com.socialnetwork.socialnetwork.entity.FriendRequest;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.repository.FriendRequestRepository;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class FriendsControllerTest extends IntegrationTestConfiguration {

    private String friendsApiURL = "/api/friends";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Test
    void testFriendsConnection() throws ResourceNotFoundException {
        User testUser1 = new User();
        testUser1.setUserSub("f3841812-e0f1-7025-b7bc-ce67d7fb933e");
        testUser1.setEmail("xanitev711@mcatag.com");
        userRepository.save(testUser1);

        User currentTestUser = jwtService.getUser();
        FriendRequest testFriendRequest = new FriendRequest();
        testFriendRequest.setFrom(testUser1);
        testFriendRequest.setTo(currentTestUser);
        friendRequestRepository.save(testFriendRequest);

        FriendRequestDTO[] niz = restTemplate.getForObject(friendsApiURL + "/requests", FriendRequestDTO[].class);

        assertThat(niz.length).isEqualTo(1);
        assertThat(niz[0].requestSender()).isEqualTo(testUser1.getEmail());
    }
}