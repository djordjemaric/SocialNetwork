package com.socialnetwork.socialnetwork.exceptions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record ExceptionResponse(
        ErrorCode errorCode,
        @NotBlank String message,
        @NotNull String timestamp
        ) {
}
