package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.SocialNetworkApplication;
import com.socialnetwork.socialnetwork.dto.friendRequest.FriendRequestDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@Transactional
@SpringBootTest(classes = SocialNetworkApplication.class)
class FriendsControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String friendsApiURL = "/api/friends";

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.2-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void startPostgresContainer(){
        postgres.start();
    }

    @AfterAll
    static void stopPostgresContainer(){
        postgres.stop();
    }

    @Test
    void connectionEstablished(){
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void testFriendsConnection(){
        ResponseEntity<FriendRequestDTO> responseEntity = restTemplate.getForEntity(friendsApiURL + "/requests", FriendRequestDTO.class);
        System.out.println(responseEntity);
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }
}