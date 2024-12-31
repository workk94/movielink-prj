package com.acorn.movielink.community.repository;

import com.acorn.movielink.community.dto.LikeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommunityLikeMapper {
    // 좋아요 여부 확인
    int checkLike(LikeDTO likeDTO);

    // 좋아요 추가
    void insertLike(LikeDTO likeDTO);

    // 좋아요 취소
    void deleteLike(LikeDTO likeDTO);

    // 게시글 좋아요 수 증가
    void incrementLikeCount(@Param("postId") int postId);

    // 게시글 좋아요 수 감소
    void decrementLikeCount(@Param("postId") int postId);
}