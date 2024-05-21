package com.socialnetwork.socialnetwork.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateGroupDto(
        @NotBlank
        String name,
        boolean isPublic) {


}

