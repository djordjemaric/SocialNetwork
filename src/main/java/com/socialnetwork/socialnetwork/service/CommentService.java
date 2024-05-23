package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.dto.post.CommentDTO;
import com.socialnetwork.socialnetwork.dto.post.CreateCommentDTO;
import com.socialnetwork.socialnetwork.entity.Comment;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.CommentMapper;
import com.socialnetwork.socialnetwork.repository.*;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final JwtService jwtService;
    private final GroupMemberRepository groupMemberRepository;
    private final FriendsRepository friendsRepository;


    public CommentService(CommentRepository commentRepository, PostRepository postRepository, CommentMapper commentMapper, JwtService jwtService, GroupMemberRepository groupMemberRepository, FriendsRepository friendshipRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.commentMapper = commentMapper;
        this.jwtService = jwtService;
        this.groupMemberRepository = groupMemberRepository;
        this.friendsRepository = friendshipRepository;
    }

    public CommentDTO createComment(Integer postId, CreateCommentDTO commentDTO) {
        User currentUser = jwtService.getUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("The post with the id of " +
                        postId + " is not present in the database."));
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
        Comment comment = commentMapper.createCommentDTOtoComment(currentUser, post, commentDTO);
        Comment savedComment = commentRepository.save(comment);
        return new CommentDTO(savedComment.getId(), savedComment.getText(), savedComment.getPost().getId(), savedComment.getCommOwner().getId());

    }

}