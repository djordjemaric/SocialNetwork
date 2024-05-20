package com.socialnetwork.socialnetwork.dto.post;

import jakarta.validation.constraints.NotBlank;

public record UpdatePostDTO(
        boolean isPublic,
        String text,
        String imgUrl) {
}
