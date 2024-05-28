package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.user.LoginResponse;
import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.dto.user.UserRequest;
import com.socialnetwork.socialnetwork.exceptions.IAMProviderException;
import com.socialnetwork.socialnetwork.service.UserService;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.BusinessLogicException;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        return this.userService.getAllUsers();
    }

    @GetMapping(path = "{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User getUser(@PathVariable Integer userId) throws ResourceNotFoundException {
        return userService.getUserById(userId);
    }

    @PostMapping(path = "/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public PreviewUserDTO createUser(@RequestBody UserRequest userRequest) throws IAMProviderException, BusinessLogicException {
        return userService.createUser(userRequest.email(), userRequest.password());
    }

    @PostMapping(path = "/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse loginUser(@RequestBody UserRequest userRequest) throws IAMProviderException {
        return userService.loginUser(userRequest.email(), userRequest.password());
    }
}


