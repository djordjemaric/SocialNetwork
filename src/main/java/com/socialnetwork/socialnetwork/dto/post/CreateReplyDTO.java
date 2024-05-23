package com.socialnetwork.socialnetwork.dto.post;

import jakarta.validation.constraints.NotBlank;

public record CreateReplyDTO(@NotBlank String text) {}