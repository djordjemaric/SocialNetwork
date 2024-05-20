package com.socialnetwork.socialnetwork.dto.post;

import com.socialnetwork.socialnetwork.entity.Comment;

import java.util.List;

public record GetPostDTO (
        String text,
        String imgUrl,
        String userEmail,
        String groupName,
        List<Comment> comments) {
}
