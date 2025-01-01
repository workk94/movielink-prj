package com.acorn.movielink.comunity.service;


import com.acorn.movielink.comunity.dto.CommentDTO;
import com.acorn.movielink.comunity.repository.CommunityCommentMapper;
import com.acorn.movielink.comunity.repository.CommunityPostMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommunityCommentMapper commentMapper;

    @Autowired
    private CommunityPostMapper postMapper;

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);


    // 로그인 검증 유틸리티
    @Transactional
    private void checkLogin(Integer memId) {
        if (memId == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }
    }

    // 게시글 존재 여부 검증
     @Transactional
    public boolean validatePostExists(int postId) {
        int count = postMapper.countPostById(postId);
        if (count == 0) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }
         return false;
     }

    // 댓글 존재 여부 검증
    public  boolean  validateCommentExists(int commentId) {
        // 한 번의 쿼리만 실행
        int count = commentMapper.countCommentById(commentId);

        if (count == 0) { // 존재하지 않는 경우 예외 발생
            throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        }
        return true; // 존재하는 경우 true 반환
    }



    @Transactional(readOnly = true)
    public boolean isCommentOwner(int commentId, int memId) {
        return commentMapper.findOwnerById(commentId) == memId;
    }
    @Transactional(readOnly = true)
    public boolean isParentChildRelation(int parentId, int childId) {
        Integer storedParentId = commentMapper.findParentIdById(childId);
        return storedParentId != null && storedParentId.equals(parentId);
    }

    // 작성자 검증
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

    @Transactional(readOnly = true)
    public boolean existsById(int commentId) {
        return commentMapper.countById(commentId) > 0;
    }

    public int getCommentCountByPostId(int postId) {
        return commentMapper.countCommentsByPostId(postId);
    }

    // 댓글 작성
    @Transactional(propagation = Propagation.REQUIRED)
    public CommentDTO addComment(CommentDTO commentDTO, Integer memId, int postId) {
        // 로그인 검증
        checkLogin(memId);
//        validatePostExists(postId);

        // 댓글 추가
        Map<String, Object> params = new HashMap<>();
        params.put("postId", commentDTO.getPostId());
        params.put("memId", memId);
        params.put("content", commentDTO.getContent());

        commentDTO.setMemId(memId); // 로그인 사용자 ID 설정
        commentMapper.addComment(params); // 직접 DTO 사용

        // commentId 반환 처리 (안전한 변환)
        Object generatedId = params.get("commentId");
        Integer newCommentId = null;

        if (generatedId != null) { // null 처리
            if (generatedId instanceof BigInteger) {
                newCommentId = ((BigInteger) generatedId).intValue();
            } else if (generatedId instanceof Integer) {
                newCommentId = (Integer) generatedId;
            } else {
                throw new IllegalArgumentException("Invalid commentId type: " + generatedId.getClass());
            }
        } else {
            throw new IllegalStateException("Failed to retrieve generated comment ID.");
        }

        // DTO 설정 및 반환
        commentDTO.setCommentId(newCommentId);
        return commentMapper.getCommentById(newCommentId); // ID 기반 댓글 조회
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




    // 댓글 수정
    @Transactional
    public CommentDTO updateComment(CommentDTO commentDTO, Integer memId) {
        // 검증 처리
        checkLogin(memId); // 로그인 검증
        validateCommentExists(commentDTO.getCommentId()); // 댓글 존재 검증
        validateAuthor(commentDTO.getCommentId(), memId); // 작성자 검증
// 댓글 수정 실행
        int updatedRows = commentMapper.updateComment(commentDTO);
        if (updatedRows == 0) {
            throw new IllegalArgumentException("댓글 수정 실패: 업데이트된 행이 없습니다.");
        }
        return commentMapper.getCommentById(commentDTO.getCommentId());
    }

    // 댓글 존재 여부 검증 메서드 수정
    public boolean validateCommentExists2(int postId, int commentId) {
        // 매퍼 호출
        int count = commentMapper.countCommentByPostAndId(postId, commentId);
        return count > 0; // 결과가 0보다 크면 존재
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




}