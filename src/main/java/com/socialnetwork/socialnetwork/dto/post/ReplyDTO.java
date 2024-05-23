package com.socialnetwork.socialnetwork.dto.post;

public record ReplyDTO(Integer ownerId,
                       String text,
                       CommentDTO comment,
                       Integer userId) {}
