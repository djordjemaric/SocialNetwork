package com.socialnetwork.socialnetwork.dto.friends;

import jakarta.validation.constraints.NotBlank;

public record DeletedFriendDTO(
        @NotBlank String deletedFriend
) {
}
