package com.socialnetwork.socialnetwork.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;

public record UpdatePostDTO(
        boolean isPublic,
        @NotBlank
        String text,
        MultipartFile img) {
}
