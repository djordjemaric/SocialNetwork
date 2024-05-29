package com.socialnetwork.socialnetwork.dto.friendRequest;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FriendRequestDTO(
        @NotNull(message = "friend request id should not be null")
        Integer id,
        @NotBlank(message = "request sender should not be null")
        String requestSender
) {
}
