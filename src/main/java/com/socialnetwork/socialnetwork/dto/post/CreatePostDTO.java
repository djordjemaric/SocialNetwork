package com.socialnetwork.socialnetwork.dto.post;

import jakarta.validation.constraints.NotBlank;

public record CreatePostDTO(
        @NotBlank
        boolean isPublic,
        String text,
        String imgUrl,
        Integer idGroup) {
}
