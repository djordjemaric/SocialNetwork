package com.socialnetwork.socialnetwork.dto.post;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommentDTO(
        @NotBlank(message = "text should not be blank")
        String text
) {}
