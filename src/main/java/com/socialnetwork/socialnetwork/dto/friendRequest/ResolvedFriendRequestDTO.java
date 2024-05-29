package com.socialnetwork.socialnetwork.dto.friendRequest;

import jakarta.validation.constraints.NotBlank;

public record ResolvedFriendRequestDTO(
        @NotBlank(message = "message should not be blank")
        String message
) {
}
