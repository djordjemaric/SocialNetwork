package com.socialnetwork.socialnetwork.dto.group;

import jakarta.validation.constraints.NotBlank;

public record CreateGroupDTO(
                             @NotBlank
                             String name,
                             boolean isPublic

) {
}
