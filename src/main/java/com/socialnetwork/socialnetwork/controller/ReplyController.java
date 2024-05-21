package com.socialnetwork.socialnetwork.controller;

import com.socialnetwork.socialnetwork.dto.post.CreateCommentDTO;
import com.socialnetwork.socialnetwork.dto.post.CreateReplyDTO;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.service.ReplyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/replies")
public class ReplyController {

    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }


    @PostMapping("/")
    public void save(@RequestBody User owner, @RequestBody CreateReplyDTO replyDTO) {
        replyService.createReply(owner, replyDTO);
    }
}