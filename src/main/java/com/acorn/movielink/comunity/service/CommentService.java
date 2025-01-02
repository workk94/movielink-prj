package com.acorn.movielink.comunity.service;


import com.acorn.movielink.comunity.controller.AuthenticationUtil;
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

    @Autowired
    private AuthenticationUtil authenticationUtil;

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
        return postMapper.countPostById(postId) > 0;
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
        validatePostExists(postId);

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



    // 댓글 수정
@Transactional(propagation = Propagation.REQUIRED)
public CommentDTO updateComment(CommentDTO commentDTO, int postId, int memId) {
    memId = authenticationUtil.getCurrentUserId();

    System.out.println("@@@@작성자 검증: " + memId);

    // 댓글 ID가 null인 경우 예외 처리
    if (commentDTO.getCommentId() == null) {
        throw new IllegalArgumentException("댓글 ID가 누락되었습니다.");
    }
    // 댓글-게시글 관계 확인
    int commentCount = commentMapper.countCommentByPostAndId(postId, commentDTO.getCommentId());
    System.out.println("댓글-게시글 관계 확인: " + commentCount);
    if (commentCount == 0) {
        throw new IllegalArgumentException("댓글이 존재하지 않거나 잘못된 요청입니다 요청입니다.");
    }
    // 작성자 검증
    int commentOwner = commentMapper.checkCommentOwner(commentDTO.getCommentId(), memId);
    System.out.println("작성자 검증: " + commentOwner);
    if (commentOwner == 0) {
        throw new SecurityException("댓글 수정 권한이 없습니다.");
    }

    // 댓글 수정 실행
    int updatedRows = commentMapper.updateComment(commentDTO);
    System.out.println("댓글 수정된 행 수: " + updatedRows);

    if (updatedRows == 0) {
        throw new IllegalStateException("댓글 수정에 실패했습니다.");
    }
    System.out.println("댓글 수정 결과: " + updatedRows);

    // 5. 수정된 댓글 반환
    return commentMapper.getCommentById(commentDTO.getCommentId());
}


}