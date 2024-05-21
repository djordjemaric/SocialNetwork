package com.socialnetwork.socialnetwork.dto.post;


public record CreateCommentDTO(String text,
                               Integer idPost,
                               Integer idCommOwner) {

    public Integer getIdOwner() {
        return idCommOwner;
    }

    public Integer getIdPost() {
        return idPost;
    }
}
