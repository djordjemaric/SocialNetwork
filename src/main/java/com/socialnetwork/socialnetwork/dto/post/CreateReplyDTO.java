package com.socialnetwork.socialnetwork.dto.post;

import jakarta.validation.constraints.NotBlank;

public record CreateReplyDTO(
        @NotBlank(message = "text should not be blank")
        String text
) {}