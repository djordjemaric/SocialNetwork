package com.socialnetwork.socialnetwork.dto.post;


import java.util.List;

public record CommentDTO(
        Integer id,
        String text,
        Integer idPost,
        Integer userId,
        List<ReplyDTO> replies

) {}