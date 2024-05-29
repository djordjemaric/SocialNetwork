package com.socialnetwork.socialnetwork.dto.friendRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SentFriendRequestDTO(
        @NotBlank(message = "friends email should not be blank")
        @Email(message = "friend's email needs to contain '@' and .domainName")
        String friendsEmail
) {}
