package com.socialnetwork.socialnetwork.service;



import com.socialnetwork.socialnetwork.dto.LoginResponse;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;



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
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new FunctionArgumentException("Invalid email");
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new FunctionArgumentException("Invalid id");
        }
    }


    public boolean createUser(String email, String password) {

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            //TODO: return status code indicating this
            throw new FunctionArgumentException("User already exists");
        }
        boolean registered = cognitoService.registerUser(email, email, password);
        if (!registered) {
            return false;
        }
        User user = new User();
        user.setEmail(email);
        userRepository.save(user);
        return true;
    }

    public Optional<LoginResponse> loginUser(String email, String password) {

        Optional<LoginResponse> responseOptional = cognitoService.loginUser(email, password);
        return responseOptional;
    }


}

