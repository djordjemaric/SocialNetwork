package com.socialnetwork.socialnetwork.dto.friendRequest;

import jakarta.validation.constraints.NotBlank;

public record PreviewFriendRequestDTO(
        @NotBlank
        String sender,
        @NotBlank
        String receiver
) {
}
