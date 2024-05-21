package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.post.CreateReplyDTO;
import com.socialnetwork.socialnetwork.entity.Comment;
import com.socialnetwork.socialnetwork.entity.Reply;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.stereotype.Component;


@Component
public class ReplyMapper {
    public Reply createReplyDTOtoReply(User owner, Comment comment, CreateReplyDTO replyDTO){
        Reply reply=new Reply();
        reply.setReplyOwner(owner);
        reply.setComment(comment);
        reply.setText(replyDTO.text());
        return reply;
    }

}
