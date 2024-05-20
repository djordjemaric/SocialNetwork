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
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userSub = (String)jwt.getClaim("sub");
        return userRepository.findByUserSub(userSub).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
