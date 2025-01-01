package com.acorn.movielink.comunity.repository;

import com.acorn.movielink.comunity.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommunityCommentMapper {


    // 댓글 목록 조회 (게시글 ID 기준 최상위 댓글만 조회)
    List<CommentDTO> getCommentsByPostId(@Param("postId") int postId);

    // 대댓글 목록 조회 (부모 댓글 ID 기준 조회)
    List<CommentDTO> getRepliesByParentId(@Param("parentId") int parentId);

    int countById(@Param("commentId") int commentId);

    // 댓글 소유자 확인
    int findOwnerById(@Param("commentId") int commentId);

    Integer findParentIdById(@Param("commentId") int commentId);


    // 댓글 작성
//    int addComment(@Param("comment") CommentDTO comment);
    void addComment(Map<String, Object> params);

    // 대댓글 작성
    int addReply(@Param("comment") CommentDTO comment);


    // 작성 후 해당 댓글 조회
    CommentDTO getCommentById(@Param("commentId") int commentId);



    // 댓글 존재 여부 검증
    int countCommentById(@Param("commentId") int commentId);

    // 특정 게시글의 댓글 수 조회
    int countCommentsByPostId(@Param("postId") int postId);

    // 댓글 작성자 검증
    Integer getAuthorByCommentId(@Param("commentId") int commentId);

//    특정 댓글의 대댓글 수를 조회
    int getReplyCountByCommentId(@Param("parentId") int parentId);


    // 댓글 수정
    int updateComment(CommentDTO commentDTO);

    // 대댓글 수정
    int updateReply(CommentDTO commentDTO);

    // 댓글 삭제 (논리 삭제 처리)
    int deleteComment(@Param("commentId") int commentId, @Param("memId") int memId);

    // 대댓글 삭제 (논리 삭제)
    int deleteReply(@Param("commentId") int commentId, @Param("memId") int memId);

    // 댓글 좋아요 증가
    int increaseLikeCount(@Param("commentId") int commentId);

    // 댓글 좋아요 감소
    int decreaseLikeCount(@Param("commentId") int commentId);

}