package com.socialnetwork.socialnetwork.dto.friendRequest;

import jakarta.validation.constraints.NotBlank;

public record ResolvedFriendRequestDTO(
        @NotBlank String message
) {
}
