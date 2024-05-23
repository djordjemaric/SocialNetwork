package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.post.CommentDTO;
import com.socialnetwork.socialnetwork.dto.post.CreateReplyDTO;
import com.socialnetwork.socialnetwork.dto.post.ReplyDTO;
import com.socialnetwork.socialnetwork.entity.Comment;
import com.socialnetwork.socialnetwork.entity.Reply;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.stereotype.Component;


@Component
public class ReplyMapper {
    private final CommentMapper commentMapper;

    public ReplyMapper(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public Reply createReplyDTOtoReply(User owner, Comment comment, CreateReplyDTO replyDTO){
        Reply reply=new Reply();
        reply.setOwner(owner);
        reply.setComment(comment);
        reply.setText(replyDTO.text());
        return reply;
    }

    public ReplyDTO replytoReplyDTO(Reply reply){
        CommentDTO commentDTO=commentMapper.commentToCommentDTO(reply.getComment());
        return new ReplyDTO(reply.getId(), reply.getText(), commentDTO, reply.getOwner().getId());
    }

}
