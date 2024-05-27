package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.post.CommentDTO;
import com.socialnetwork.socialnetwork.dto.post.CreateCommentDTO;
import com.socialnetwork.socialnetwork.entity.Comment;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {


    public Comment commentDTOtoComment(User owner, Post post, CreateCommentDTO commentDTO){
        Comment comment = new Comment();
        comment.setCommOwner(owner);
        comment.setPost(post);
        comment.setText(commentDTO.text());
        return comment;
    }

    public CommentDTO commentToCommentDTO(Comment comment){
        return new CommentDTO(comment.getId(), comment.getText(), comment.getPost().getId(), comment.getCommOwner().getId());
    }
}
