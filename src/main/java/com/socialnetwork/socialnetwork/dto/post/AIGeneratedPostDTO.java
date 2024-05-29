package com.socialnetwork.socialnetwork.dto.post;

import jakarta.validation.constraints.NotBlank;

public record AIGeneratedPostDTO(
        boolean isPublic,
        @NotBlank(message = "text prompt should not be blank")
        String txtPrompt,
        String imgPrompt,
        Integer idGroup) {
}
