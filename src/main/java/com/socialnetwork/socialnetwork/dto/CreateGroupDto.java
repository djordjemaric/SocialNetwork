package com.socialnetwork.socialnetwork.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateGroupDto(
        @NotBlank
        String name,
        boolean isPublic) {


}
