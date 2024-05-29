package com.socialnetwork.socialnetwork.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(@NotBlank
                          String email,
                          @NotBlank
                          String password) {}
