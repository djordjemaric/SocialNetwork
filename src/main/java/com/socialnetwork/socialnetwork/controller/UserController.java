package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.LoginResponse;
import com.socialnetwork.socialnetwork.dto.UserRequest;
import com.socialnetwork.socialnetwork.service.UserService;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/users")
    public List<User> getAllUsers() {
        return this.userService.getAllUsers();
    }

    @GetMapping(path = "{userId}")
    public User getUser(@PathVariable Integer userId) {
        return userService.getUserById(userId);
    }

    @PostMapping(path = "/signup")
    public User createUser(@RequestBody UserRequest userRequest) {
        return userService.createUser(userRequest.email(), userRequest.password());
    }

    @PostMapping(path = "/login")
    public LoginResponse loginUser(@RequestBody UserRequest userRequest) {
        return userService.loginUser(userRequest.email(), userRequest.password());
    }
}


