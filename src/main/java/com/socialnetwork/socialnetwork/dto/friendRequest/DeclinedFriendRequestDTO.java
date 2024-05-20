package com.socialnetwork.socialnetwork.dto.friendRequest;

import jakarta.validation.constraints.NotBlank;

public record DeclinedFriendRequestDTO(
        @NotBlank String message
) {
}
