package com.socialnetwork.socialnetwork.dto.friendRequest;

import jakarta.validation.constraints.NotBlank;

public record PreviewFriendRequestDTO(
        @NotBlank(message = "sender should not be blank")
        String sender,
        @NotBlank(message = "receiver should not be blank")
        String receiver
) {
}
