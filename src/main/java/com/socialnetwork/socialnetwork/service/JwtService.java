package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.entity.User;
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

    public User getUser(){
        String userSub = getUserSub();
        return userRepository.findByUserSub(userSub)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String getUserSub(){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getClaim("sub");
    }
}
