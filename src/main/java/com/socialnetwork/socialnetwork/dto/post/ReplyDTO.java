package com.socialnetwork.socialnetwork.dto.post;

public record ReplyDTO(Integer id,
                       String text,
                       Integer commentId,
                       Integer userId) {}
