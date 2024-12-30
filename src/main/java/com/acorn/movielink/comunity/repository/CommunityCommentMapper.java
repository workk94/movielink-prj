package com.acorn.movielink.comunity.repository;

import com.acorn.movielink.comunity.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommunityCommentMapper {
    // 댓글 목록 조회 (게시글에 달린 댓글)
    List<CommentDTO> getCommentsByPostId(int postId);

    // 대댓글 조회
    List<CommentDTO> getRepliesByParentId(int parentId);

    // 댓글 작성
    void insertComment(CommentDTO commentDTO);

    // 대댓글 작성
    void insertReply(CommentDTO commentDTO);

    // 댓글 수정
    void updateComment(CommentDTO commentDTO);
    // 대댓글 수정
    void updateReply(CommentDTO commentDTO);

    // 댓글 삭제 (논리 삭제)
    void deleteComment(int commentId);

    // 대댓글 삭제 (논리 삭제)
    void deleteReply(int commentId);

    // 댓글 좋아요 증가
    void increaseLikeCount(int commentId);

    // 댓글 좋아요 취소
    void decreaseLikeCount(int commentId);

    // 특정 게시글에 대한 댓글 수 조회
    int getCommentCountByPostId(int postId);

    // 특정 댓글에 대한 대댓글 수 조회
    int getReplyCountByCommentId(int commentId);
}