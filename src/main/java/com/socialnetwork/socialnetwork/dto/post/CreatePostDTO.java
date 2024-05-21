package com.socialnetwork.socialnetwork.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreatePostDTO(
        boolean isPublic,
        @NotBlank
        String text,
        @Pattern(regexp = "^(?!\\s*$).+")
        String imgUrl,
        Integer idGroup) {
}
