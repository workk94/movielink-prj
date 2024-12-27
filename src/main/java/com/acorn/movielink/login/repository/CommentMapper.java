package com.acorn.movielink.login.repository;

import com.acorn.movielink.login.dto.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    //  특정 회원의 '전체 댓글 목록' 조회
    List<Comment> findCommentsByMemId(@Param("memId") int memId);

    //  특정 회원의 '댓글 개수' 조회
    int countCommentsByMemId(@Param("memId") int memId);
}
