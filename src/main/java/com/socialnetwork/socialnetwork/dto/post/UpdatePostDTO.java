package com.socialnetwork.socialnetwork.dto.post;

public record UpdatePostDTO(
        Integer id,
        boolean isPublic,
        String text,
        String imgUrl) {
}
