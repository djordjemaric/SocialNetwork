package com.socialnetwork.socialnetwork.dto.post;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record CreatePostDTO(
        boolean isPublic,
        @NotBlank
        String text,
        MultipartFile img,
        Integer idGroup) {
}
