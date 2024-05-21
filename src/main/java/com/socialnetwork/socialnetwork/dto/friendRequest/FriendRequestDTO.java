package com.socialnetwork.socialnetwork.dto.friendRequest;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FriendRequestDTO(
        @NotNull Integer id,
        @NotBlank String requestSender
) {
}
