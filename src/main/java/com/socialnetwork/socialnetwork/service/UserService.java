package com.socialnetwork.socialnetwork.service;



import com.socialnetwork.socialnetwork.dto.LoginResponse;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.IAMProviderException;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final CognitoService cognitoService;

    @Autowired
    public UserService(UserRepository userRepository, CognitoService cognitoService) {
        this.userRepository = userRepository;
        this.cognitoService = cognitoService;
    }

    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new FunctionArgumentException("Invalid email"));

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new FunctionArgumentException("Invalid id"));
    }


    public User createUser(String email, String password) {

        if (userRepository.existsByEmail(email)) {
            throw new FunctionArgumentException("User already exists");
        }
        String userSub = cognitoService.registerUser(email, email, password);

        User user = new User();
        user.setEmail(email);
        user.setUserSub(userSub);
        userRepository.save(user);
        return user;
    }

    public LoginResponse loginUser(String email, String password) {
        return cognitoService.loginUser(email, password);
    }


}

