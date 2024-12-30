package com.acorn.movielink.comunity.service;


import com.acorn.movielink.comunity.dto.CommentDTO;
import com.acorn.movielink.comunity.repository.CommunityCommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommunityCommentMapper commentMapper;

    // 게시글에 달린 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByPostId(int postId) {
        // 게시글에 달린 댓글 조회
        List<CommentDTO> comments = commentMapper.getCommentsByPostId(postId);

        // 각 댓글에 대한 대댓글 조회
        for (CommentDTO comment : comments) {
            List<CommentDTO> replies = commentMapper.getRepliesByParentId(comment.getCommentId());
            comment.setReplies(replies);  // 댓글 객체에 대댓글 추가
        }
        return comments;
    }

//    // **댓글 소유자 확인 메서드**
//    public boolean isOwner(int commentId, int memId) {
//        CommentDTO comment = commentMapper.findById(commentId)
//                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
//        return comment.getMemId() == memId; // 현재 사용자(memId)와 댓글 작성자 비교
//    }


    // 댓글 작성
    @Transactional
    public void addComment(CommentDTO commentDTO) {
        // 댓글 작성
        commentMapper.insertComment(commentDTO);
    }

    // 대댓글 작성
    @Transactional
    public void addReply(CommentDTO commentDTO) {
        // 대댓글 작성
        commentMapper.insertReply(commentDTO);
    }

    // 댓글 수정
    @Transactional
    public void updateComment(CommentDTO commentDTO) {
        // 댓글 수정
        commentMapper.updateComment(commentDTO);
    }

    // 대댓글 수정
    @Transactional
    public void updateReply(CommentDTO commentDTO) {
        // 대댓글 수정
        commentMapper.updateReply(commentDTO);
    }

    // 댓글 삭제 (논리 삭제)
    @Transactional
    public void deleteComment(int commentId) {
        // 댓글 삭제 (논리 삭제)
        commentMapper.deleteComment(commentId);
    }

    // 대댓글 삭제 (논리 삭제)
    @Transactional
    public void deleteReply(int commentId) {
        // 대댓글 삭제 (논리 삭제)
        commentMapper.deleteReply(commentId);
    }

    // 댓글 좋아요 증가
    @Transactional
    public void increaseLikeCount(int commentId) {
        commentMapper.increaseLikeCount(commentId);
    }

    // 댓글 좋아요 취소
    @Transactional
    public void decreaseLikeCount(int commentId) {
        commentMapper.decreaseLikeCount(commentId);
    }

    // 특정 게시글에 대한 댓글 수 조회
    @Transactional(readOnly = true)
    public int getCommentCountByPostId(int postId) {
        return commentMapper.getCommentCountByPostId(postId);
    }

    // 특정 댓글에 대한 대댓글 수 조회
    @Transactional(readOnly = true)
    public int getReplyCountByCommentId(int commentId) {
        return commentMapper.getReplyCountByCommentId(commentId);
    }

}