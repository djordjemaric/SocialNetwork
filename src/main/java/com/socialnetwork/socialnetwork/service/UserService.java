package com.socialnetwork.socialnetwork.service;



import com.socialnetwork.socialnetwork.dto.LoginResponse;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.BusinessLogicException;
import com.socialnetwork.socialnetwork.exceptions.ErrorCode;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) throws ResourceNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "No user found with id: " + id));
    }


    public User createUser(String email, String password) throws BusinessLogicException {

        if (userRepository.existsByEmail(email)) {
            throw new BusinessLogicException(ErrorCode.USER_ALREADY_EXISTS, "User already exists in db with email: " + email);
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

