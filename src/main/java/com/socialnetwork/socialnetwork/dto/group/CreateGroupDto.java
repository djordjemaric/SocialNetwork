package com.socialnetwork.socialnetwork.dto.group;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateGroupDto(
        @NotBlank
        String name,
        @NotNull
        boolean isPublic) {


}
