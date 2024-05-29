package com.socialnetwork.socialnetwork.dto.user;

public record LoginResponse(String accessToken,
                            String refreshToken,
                            Integer expiresIn) {
}
