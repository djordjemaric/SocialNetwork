package com.socialnetwork.socialnetwork.dto.friendRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SentFriendRequestDTO(
        @NotBlank
        @Email
        String friendsEmail
) {}
