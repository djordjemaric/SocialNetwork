package com.socialnetwork.socialnetwork;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.socialnetwork.socialnetwork.controller.UserController;
import com.socialnetwork.socialnetwork.dto.user.LoginResponse;
import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.dto.user.UserRequest;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTests {

    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;
    private UserRequest userRequest;
    private User user;
    private PreviewUserDTO expectedUserDTO;
    private LoginResponse expectedLoginResponse;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        userRequest = new UserRequest("user@user.com", "test");
        user = new User(1, "user@user.com", "sample-user-sub");
        expectedUserDTO = new PreviewUserDTO(user.getId(), user.getEmail());
        expectedLoginResponse = new LoginResponse("access", "refresh", 1000);
    }

    @Test
    void testCreateUserSuccess() throws Exception {

        when(userService.createUser(userRequest.email(), userRequest.password())).thenReturn(expectedUserDTO);

        String content = objectWriter.writeValueAsString(userRequest);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    PreviewUserDTO actualUserDTO = objectMapper.readValue(responseBody, PreviewUserDTO.class);
                    assertEquals(actualUserDTO, expectedUserDTO);
                });
    }

    @Test
    void testLoginUserSuccess() throws Exception {
        when(userService.loginUser(userRequest.email(), userRequest.password())).thenReturn(expectedLoginResponse);

        String content = objectWriter.writeValueAsString(userRequest);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    LoginResponse actualLoginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
                    assertEquals(actualLoginResponse, expectedLoginResponse);
                });
    }
}
