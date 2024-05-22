package com.socialnetwork.socialnetwork.dto.post;


public record CommentDTO(
        Integer id,
        String text,
        Integer idPost,
        Integer userId
        ) {}