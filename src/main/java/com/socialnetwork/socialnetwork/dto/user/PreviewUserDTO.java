package com.socialnetwork.socialnetwork.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PreviewUserDTO(
        @NotNull Integer id,
        @Email @NotBlank String email
) {
}
