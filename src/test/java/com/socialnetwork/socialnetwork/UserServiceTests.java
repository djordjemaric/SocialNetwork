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
import static org.mockito.Mockito.any;

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
    void testGetUserByIdThrowsResourceNotFoundException() {
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(2));
        verify(userRepository).findById(idArgumentCaptor.capture());
        assertEquals(2, idArgumentCaptor.getValue());
    }

    @Test
    void testGetUserByIdSuccess() throws ResourceNotFoundException {
         when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
         User returnedUser = userService.getUserById(user.getId());

         verify(userRepository).findById(idArgumentCaptor.capture());
         assertEquals(user, returnedUser);
         assertEquals(idArgumentCaptor.getValue(), user.getId());
    }

    @Test
    void testCreateUserThrowsBusinessLogicException() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
        assertThrows(BusinessLogicException.class,() -> userService.createUser(user.getEmail(),"test"));

        verify(userRepository).existsByEmail(stringArgumentCaptor.capture());
        assertEquals(stringArgumentCaptor.getValue(), user.getEmail());
    }

    @Test
    void testCreateUserSuccess() throws IAMProviderException, BusinessLogicException {
        User newUser = new User(2, "user2@user.com", "sample-user-sub");
        PreviewUserDTO expectedDTO = new PreviewUserDTO(newUser.getId(), newUser.getEmail());
        when(userMapper.userToPreviewUserDTO(any(User.class))).thenReturn(expectedDTO);
        when(userRepository.existsByEmail(any(String.class))).thenReturn(false);
        when(cognitoService.registerUser(any(String.class),
                                         any(String.class)))
                                        .thenReturn("sample-user-sub");

        PreviewUserDTO resultDTO = userService.createUser(newUser.getEmail(), "test");
        verify(userRepository).existsByEmail(stringArgumentCaptor.capture());
        verify(cognitoService).registerUser(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());
        verify(userMapper).userToPreviewUserDTO(userArgumentCaptor.capture());

        assertEquals(expectedDTO, resultDTO);
    }

    @Test
    void testLoginUserThrowsIAMProviderException() throws IAMProviderException {
        User user2  = new User(2, "not@exists.com", "sample-user-sub");
        when(cognitoService.loginUser(user2.getEmail(), "test")).thenThrow(IAMProviderException.class);

        assertThrows(IAMProviderException.class, () -> userService.loginUser("not@exists.com","test"));

        verify(cognitoService).loginUser(user2.getEmail(), "test");

    }

    @Test
    void testLoginUserSuccess() throws IAMProviderException, BusinessLogicException {
        LoginResponse loginResponseExpected = new LoginResponse("access",
                                                                "refresh",
                                                                    10);
        when(cognitoService.loginUser(user.getEmail(),
                            "test"))
                                    .thenReturn(loginResponseExpected);

        LoginResponse loginResponseResult = userService.loginUser(user.getEmail(),"test");

        verify(cognitoService).loginUser(user.getEmail(), "test");
        assertEquals(loginResponseExpected, loginResponseResult);
    }


}
