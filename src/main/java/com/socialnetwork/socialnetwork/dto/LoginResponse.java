package com.socialnetwork.socialnetwork.dto;

public record LoginResponse(String accessToken,
                            String refreshToken,
                            Integer expiresIn) {
}
