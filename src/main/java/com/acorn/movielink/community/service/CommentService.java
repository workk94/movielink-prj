package com.acorn.movielink.community.service;


import com.acorn.movielink.community.dto.CommentDTO;
import com.acorn.movielink.community.repository.CommunityCommentMapper;
import com.acorn.movielink.community.repository.CommunityPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    @Autowired
    private CommunityCommentMapper commentMapper;

    @Autowired
    private CommunityPostMapper postMapper;


    // 로그인 검증 유틸리티
    @Transactional
    private void checkLogin(Integer memId) {
        if (memId == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }
    }

    // 게시글 존재 여부 검증
    @Transactional(readOnly = true)
    private void validatePostExists(int postId) {
        int count = postMapper.countPostById(postId);
        if (count == 0) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }
    }

    // 댓글 존재 여부 검증
    @Transactional(readOnly = true)
    public boolean validateCommentExists(int commentId) {
        // 한 번의 쿼리만 실행
        int count = commentMapper.countCommentById(commentId);

        if (count == 0) { // 존재하지 않는 경우 예외 발생
            throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        }
        return true; // 존재하는 경우 true 반환
    }


    // 작성자 검증
    @Transactional(readOnly = true)
    private void validateAuthor(int commentId, int memId) {
        CommentDTO comment = commentMapper.getCommentById(commentId); // 댓글 ID로 조회
        if (comment == null || comment.getMemId() != memId) {
            throw new SecurityException("작성자만 수정/삭제할 수 있습니다.");
        }
    }


    // 게시글에 달린 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByPostId(int postId) {
        validatePostExists(postId); // 게시글 존재 여부 검증

        List<CommentDTO> comments = commentMapper.getCommentsByPostId(postId);

        for (CommentDTO comment : comments) {
            List<CommentDTO> replies = commentMapper.getRepliesByParentId(comment.getCommentId());
            replies.removeIf(reply -> reply.getCommentDeletedAt() != null); // 삭제된 대댓글 제외
            comment.setReplies(replies);
        }
        return comments;
    }




    // 댓글 작성
    @Transactional
    public CommentDTO addComment(CommentDTO commentDTO, Integer memId, int postId) {
        // 로그인 검증
        checkLogin(memId);
        validatePostExists(commentDTO.getPostId()); // 게시글 검증

        Map<String, Object> params = new HashMap<>();
        params.put("postId", commentDTO.getPostId());
        params.put("memId", memId);
        params.put("content", commentDTO.getContent());

        commentMapper.addComment(params); // 댓글 삽입
        // Key 반환 및 타입 변환
        Object generatedKey = params.get("commentId");
        int newCommentId = (generatedKey instanceof BigInteger)
                ? ((BigInteger) generatedKey).intValue()  // BigInteger 처리
                : (int) generatedKey;

        // 댓글 조회 후 반환
        return commentMapper.getCommentById(newCommentId);
    }


        // 대댓글 작성
    @Transactional
    public CommentDTO addReply(CommentDTO commentDTO, Integer memId) {
        checkLogin(memId); // 로그인 검증
        validatePostExists(commentDTO.getPostId()); // 게시글 검증
        validateCommentExists(commentDTO.getParentId()); // 부모 댓글 존재 검증


        // 부모 댓글 삭제 여부 검증
        CommentDTO parentComment = commentMapper.getCommentById(commentDTO.getParentId());
        if (parentComment.getCommentDeletedAt() != null) {
            throw new IllegalStateException("삭제된 댓글에는 대댓글을 작성할 수 없습니다.");
        }
        commentDTO.setMemId(memId); // 로그인 사용자 ID 설정

        commentMapper.addReply(commentDTO); // 대댓글 작성
        return commentMapper.getCommentById(commentDTO.getCommentId()); // 작성된 대댓글 즉시 반환
    }


    @Transactional(readOnly = true)
    public boolean isCommentOwner(int commentId, int memId) {
        return commentMapper.getAuthorByCommentId(commentId) == memId;
    }

    // 댓글 수정
    @Transactional
    public CommentDTO updateComment(CommentDTO commentDTO, Integer memId) {
        // 검증 처리
        checkLogin(memId); // 로그인 검증
        validateCommentExists(commentDTO.getCommentId()); // 댓글 존재 검증
        validateAuthor(commentDTO.getCommentId(), memId); // 작성자 검증

        // 댓글 수정
        commentMapper.updateComment(commentDTO);
        return commentMapper.getCommentById(commentDTO.getCommentId());
    }




    // 대댓글 수정
    @Transactional
    public void updateReply(CommentDTO commentDTO, Integer memId) {
        // 로그인 및 작성자 검증
        checkLogin(memId); // 로그인 검증
        validateCommentExists(commentDTO.getCommentId()); // 댓글 존재 검증
        validateAuthor(commentDTO.getCommentId(), memId); // 작성자 검증


        // 대댓글 수정
        commentMapper.updateReply(commentDTO);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(int commentId, Integer memId) {
        checkLogin(memId); // 로그인 검증
        validateCommentExists(commentId); // 존재 여부 검증
        validateAuthor(commentId, memId); // 작성자 검증

        commentMapper.deleteComment(commentId, memId); // 댓글 삭제
    }

    // 대댓글 삭제
    @Transactional
    public void deleteReply(int commentId, Integer memId) {
        checkLogin(memId); // 로그인 검증
        validateCommentExists(commentId); // 존재 여부 검증
        validateAuthor(commentId, memId); // 작성자 검증

        commentMapper.deleteReply(commentId, memId); // 대댓글 삭제
    }



    // 좋아요 증가
    @Transactional
    public void increaseLikeCount(int commentId, Integer memId) {
        checkLogin(memId); // 로그인 검증
        validateCommentExists(commentId); // 댓글 존재 여부 검증

        commentMapper.increaseLikeCount(commentId); // 좋아요 증가 쿼리 실행
    }

    // 좋아요 감소
    @Transactional
    public void decreaseLikeCount(int commentId, Integer memId) {
        checkLogin(memId); // 로그인 검증
        validateCommentExists(commentId); // 댓글 존재 여부 검증

        commentMapper.decreaseLikeCount(commentId); // 좋아요 감소 쿼리 실행
    }

    @Transactional(readOnly = true)
    public boolean isParentChildRelation(int parentId, int childId) {
        Integer storedParentId = commentMapper.findParentIdById(childId);
        return storedParentId != null && storedParentId.equals(parentId);
    }


}