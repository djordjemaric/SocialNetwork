package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.ErrorCode;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser() throws ResourceNotFoundException {
        String userSub = getUserSub();
        return userRepository.findByUserSub(userSub)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERROR_FINDING_USER_BY_JWT,"User not found. Wrong JWT token"));

    }

    public String getUserSub(){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getClaim("sub");
    }
}
