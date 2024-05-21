package com.socialnetwork.socialnetwork.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(@NotBlank
                          String email,
                          @NotBlank
                          String password) {}