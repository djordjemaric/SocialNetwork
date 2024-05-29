package com.socialnetwork.socialnetwork;

import com.socialnetwork.socialnetwork.dto.user.LoginResponse;
import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.BusinessLogicException;
import com.socialnetwork.socialnetwork.exceptions.IAMProviderException;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.mapper.UserMapper;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import com.socialnetwork.socialnetwork.service.CognitoService;
import com.socialnetwork.socialnetwork.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private CognitoService cognitoService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    private ArgumentCaptor<Integer> idArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;



    @BeforeEach
    void setUp() {
        user = new User(1, "user1@user.com", "");
    }

    @Test
    void getUserById_userDoesNotExist_throwsException() {
        when(userRepository.findById(idArgumentCaptor.capture())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(2));
        assertEquals(2, idArgumentCaptor.getValue());
    }

    @Test
    void getUserById_userExists_success() {
         when(userRepository.findById(idArgumentCaptor.capture())).thenReturn(Optional.of(user));
         User returnedUser = userRepository.findById(user.getId()).get();
         assertEquals(user, returnedUser);
         assertEquals(idArgumentCaptor.getValue(), user.getId());
    }

    @Test
    void createUser_userExists_throwsException() {
        when(userRepository.existsByEmail(stringArgumentCaptor.capture())).thenReturn(true);
        assertThrows(BusinessLogicException.class,() -> userService.createUser(user.getEmail(),"test"));
        assertEquals(stringArgumentCaptor.getValue(), user.getEmail());

        verify(userRepository).existsByEmail(stringArgumentCaptor.capture());
    }

    @Test
    void createUser_success() throws IAMProviderException, BusinessLogicException {
        User newUser = new User(2, "user2@user.com", "sample-user-sub");
        PreviewUserDTO expectedDTO = new PreviewUserDTO(newUser.getId(), newUser.getEmail());
        when(userMapper.userToPreviewUserDTO(userArgumentCaptor.capture())).thenReturn(expectedDTO);
        when(userRepository.existsByEmail(stringArgumentCaptor.capture())).thenReturn(false);
        when(cognitoService.registerUser(stringArgumentCaptor.capture(),
                                         stringArgumentCaptor.capture()))
                                        .thenReturn("sample-user-sub");

        PreviewUserDTO resultDTO = userService.createUser(newUser.getEmail(), "test");

        assertEquals(expectedDTO.email(), resultDTO.email());
        verify(userRepository).existsByEmail(stringArgumentCaptor.getAllValues().get(0));
        verify(cognitoService).registerUser(stringArgumentCaptor.getAllValues().get(0), "test");

        assertEquals(stringArgumentCaptor.getAllValues().get(0), newUser.getEmail());
        assertEquals(stringArgumentCaptor.getAllValues().get(2), "test");
    }

    @Test
    void loginUser_userDoesNotExist_throwsException() throws IAMProviderException {
        when(cognitoService.loginUser(stringArgumentCaptor.capture(), stringArgumentCaptor.capture())).thenThrow(IAMProviderException.class);
        assertThrows(IAMProviderException.class, () -> userService.loginUser("not@exists.com","test"));

        verify(cognitoService).loginUser(stringArgumentCaptor.getAllValues().get(0), stringArgumentCaptor.getAllValues().get(1));
        assertEquals(stringArgumentCaptor.getAllValues().get(0), "not@exists.com");
        assertEquals(stringArgumentCaptor.getAllValues().get(1), "test");
    }

    @Test
    void loginUser_userExists_success() throws IAMProviderException, BusinessLogicException {
        LoginResponse loginResponseExpected = new LoginResponse("access", "refresh", 10);
        when(cognitoService.loginUser(stringArgumentCaptor.capture(),
                                      stringArgumentCaptor.capture()))
                                      .thenReturn(loginResponseExpected);

        LoginResponse loginResponseResult = userService.loginUser(user.getEmail(),"test");

        verify(cognitoService).loginUser(stringArgumentCaptor.getAllValues().get(0), stringArgumentCaptor.getAllValues().get(1));
        assertEquals(loginResponseExpected.accessToken(), loginResponseResult.accessToken());
        assertEquals(loginResponseExpected.refreshToken(), loginResponseResult.refreshToken());
        assertEquals(loginResponseExpected.expiresIn(), loginResponseResult.expiresIn());


    }


}
