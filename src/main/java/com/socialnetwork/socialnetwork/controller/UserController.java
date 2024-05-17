package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.LoginResponse;
import com.socialnetwork.socialnetwork.dto.UserRequest;
import com.socialnetwork.socialnetwork.service.UserService;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
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
    public void createUser(@RequestBody UserRequest userRequest) {
        userService.createUser(userRequest.email(), userRequest.password());
    }

    @PostMapping(path = "/login")
    public LoginResponse loginUser(@RequestBody UserRequest userRequest) {
        Optional<LoginResponse> responseOptional = userService.loginUser(userRequest.email(), userRequest.password());
        if (responseOptional.isPresent()) {
            return responseOptional.get();
        } else {
            throw new RuntimeException("Failed login.");
        }
    }
}


