package com.socialnetwork.socialnetwork;

import com.socialnetwork.socialnetwork.entity.FriendRequest;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.repository.*;
import com.socialnetwork.socialnetwork.service.JwtService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@Testcontainers
@TestPropertySource(properties = {
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://issuer/.well-known/jwks.json"
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class IntegrationTestConfiguration {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected GroupMemberRepository groupMemberRepository;

    @Autowired
    protected GroupRequestRepository groupRequestRepository;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected ReplyRepository replyRepository;

    @Autowired
    protected FriendRequestRepository friendRequestRepository;

    @Autowired
    protected FriendsRepository friendsRepository;

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected GroupRepository groupRepository;

    @ServiceConnection
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.2-alpine").withReuse(true);
    @MockBean
    protected JwtService jwtService;

    @BeforeEach
    void setUp() throws ResourceNotFoundException {
        emptyDatabase();
        User testUser = new User();
        testUser.setEmail("vica.ristic@gmail.com");
        testUser.setUserSub("93246812-3021-704e-9c37-bf46100f22dc");
        testUser=userRepository.save(testUser);
        when(jwtService.getUser()).thenReturn(testUser);
    }

    @AfterEach
    void cleanDatabase() {
        postRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeAll
    public static void beforeAll() {
        postgres.start();
    }


    public void emptyDatabase(){
        friendRequestRepository.deleteAll();
        friendsRepository.deleteAll();
        groupMemberRepository.deleteAll();
        groupRequestRepository.deleteAll();
        replyRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }
}
