package com.socialnetwork.socialnetwork.dto.post;

public record CreatePostDTO(
        boolean isPublic,
        String text,
        String imgUrl,
        Integer idGroup) {
}
