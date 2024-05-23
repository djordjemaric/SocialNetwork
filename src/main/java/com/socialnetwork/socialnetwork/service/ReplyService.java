package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.post.CommentDTO;
import com.socialnetwork.socialnetwork.dto.post.CreateReplyDTO;
import com.socialnetwork.socialnetwork.dto.post.ReplyDTO;
import com.socialnetwork.socialnetwork.entity.Comment;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.Reply;
import com.socialnetwork.socialnetwork.entity.User;

import com.socialnetwork.socialnetwork.mapper.ReplyMapper;
import com.socialnetwork.socialnetwork.repository.*;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@Service
public class ReplyService {
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final ReplyMapper replyMapper;
    private final JwtService jwtService;
    private final PostRepository postRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final FriendsRepository friendsRepository;

    public ReplyService(CommentRepository commentRepository, ReplyRepository replyRepository, ReplyMapper replyMapper, JwtService jwtService, PostRepository postRepository, GroupMemberRepository groupMemberRepository, FriendsRepository friendsRepository) {
        this.commentRepository = commentRepository;
        this.replyRepository = replyRepository;
        this.replyMapper = replyMapper;
        this.jwtService = jwtService;
        this.postRepository = postRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.friendsRepository = friendsRepository;
    }

//check if there’s comment with the given combination postId and commentId (from endpoint path)
// to be sure that the reply will be saved on the right comment
    public ReplyDTO createReply(Integer commentId, CreateReplyDTO replyDTO) {
        User currentUser = jwtService.getUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("The comment with the id of " + commentId + " is not present in the database."));

        Post post = comment.getPost();
        if (post == null) {
            throw new NoSuchElementException("The post associated with the comment is not present in the database.");
        }

        if (!post.isPublic() && post.getGroup() == null) {
            if (friendsRepository.areTwoUsersFriends(post.getOwner().getId(), currentUser.getId()).isEmpty()) {
                throw new RuntimeException("You cannot see the post because you are not friends with the post owner.");
            }
        }
        if (post.getGroup() != null && !(post.getGroup().isPublic())) {
            if (!(groupMemberRepository.existsByUserIdAndGroupId(currentUser.getId(), post.getGroup().getId()))) {
                throw new RuntimeException("You cannot see the post because you are not a member of the "
                        + post.getGroup().getName() + " group.");
            }
        }
        Reply reply = replyMapper.createReplyDTOtoReply(currentUser, comment, replyDTO);
        Reply savedReply = replyRepository.save(reply);
        return new ReplyDTO(savedReply.getId(), savedReply.getText(), savedReply.getComment().getId(), savedReply.getReplyOwner().getId());

    }
}
