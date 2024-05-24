package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.LoginResponse;
import com.socialnetwork.socialnetwork.dto.UserRequest;
import com.socialnetwork.socialnetwork.exceptions.BusinessLogicException;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.service.UserService;
import com.socialnetwork.socialnetwork.entity.User;
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
    public List<User> getAllUsers() {
        return this.userService.getAllUsers();
    }

    @GetMapping(path = "{userId}")
    public User getUser(@PathVariable Integer userId) {
        try {
            return userService.getUserById(userId);
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(path = "/signup")
    public User createUser(@RequestBody UserRequest userRequest) {
        try {
            return userService.createUser(userRequest.email(), userRequest.password());
        } catch (BusinessLogicException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(path = "/login")
    public LoginResponse loginUser(@RequestBody UserRequest userRequest) {
        return userService.loginUser(userRequest.email(), userRequest.password());
    }
}


