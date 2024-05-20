package com.socialnetwork.socialnetwork.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePostDTO(
        @NotNull
        Integer id,
        @NotBlank
        boolean isPublic,
        String text,
        String imgUrl) {
}
