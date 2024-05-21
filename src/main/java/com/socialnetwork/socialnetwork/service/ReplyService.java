package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.post.CreateReplyDTO;
import com.socialnetwork.socialnetwork.entity.Comment;
import com.socialnetwork.socialnetwork.entity.User;

import com.socialnetwork.socialnetwork.mapper.ReplyMapper;
import com.socialnetwork.socialnetwork.repository.CommentRepository;
import com.socialnetwork.socialnetwork.repository.ReplyRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@Service
public class ReplyService {
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final ReplyMapper replyMapper;

    public ReplyService(CommentRepository commentRepository, ReplyRepository replyRepository, ReplyMapper replyMapper) {
        this.commentRepository = commentRepository;
        this.replyRepository = replyRepository;
        this.replyMapper = replyMapper;
    }



    public void createReply(User owner, CreateReplyDTO replyDTO) {
        Comment comment = commentRepository.findById(replyDTO.idComm()).orElseThrow(
                ()->new NoSuchElementException("There is no post with the id of "+replyDTO.idComm()));
        replyRepository.save(replyMapper.createReplyDTOtoReply(owner,comment, replyDTO));
    }
}
