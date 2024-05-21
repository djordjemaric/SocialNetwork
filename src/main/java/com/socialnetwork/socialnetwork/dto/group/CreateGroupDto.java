package com.socialnetwork.socialnetwork.dto.group;


import jakarta.validation.constraints.NotBlank;

public record CreateGroupDto(
        @NotBlank
        String name,
        boolean isPublic) {


}
