package com.socialnetwork.socialnetwork.dto.post;

import jakarta.validation.constraints.NotBlank;

public record OpenAIPostDTO(
        boolean isPublic,
        @NotBlank
        String txtPrompt,
        String imgPrompt,
        Integer idGroup) {
}
