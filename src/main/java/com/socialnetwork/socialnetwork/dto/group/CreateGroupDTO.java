package com.socialnetwork.socialnetwork.dto.group;

import jakarta.validation.constraints.NotBlank;

public record CreateGroupDTO(
        @NotBlank(message = "group name should not be blank")
        String name,
        boolean isPublic) {
}
