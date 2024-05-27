package com.socialnetwork.socialnetwork.dto.post;

import jakarta.validation.constraints.NotBlank;

public record AIGeneratedPostDTO(
        boolean isPublic,
        @NotBlank
        String txtPrompt,
        String imgPrompt,
        Integer idGroup) {
}
