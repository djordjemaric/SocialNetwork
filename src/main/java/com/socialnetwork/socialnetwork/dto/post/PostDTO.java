package com.socialnetwork.socialnetwork.dto.post;

import com.socialnetwork.socialnetwork.entity.Comment;

import java.util.List;

public record PostDTO(
        Integer id,
        String text,
        String imgUrl,
        String userEmail,
        String groupName,
        List<CommentDTO> comments) {

}