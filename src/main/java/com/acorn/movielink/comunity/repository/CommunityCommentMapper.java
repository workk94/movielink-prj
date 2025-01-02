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
    void addComment(Map<String, Object> params);

    // 작성 후 해당 댓글 조회
    CommentDTO getCommentById(@Param("commentId") int commentId);

    // 댓글 존재 여부 검증
    int countCommentById(@Param("commentId") int commentId);

    // 특정 게시글의 댓글 수 조회
    int countCommentsByPostId(@Param("postId") int postId);


    // 댓글 수정
    int updateComment(CommentDTO commentDTO);

    int countCommentByPostAndId(@Param("postId") int postId, @Param("commentId") int commentId);
    // 작성자 검증
    int checkCommentOwner(@Param("commentId") int commentId, @Param("memId") int memId);


}