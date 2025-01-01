package com.acorn.movielink.movie_detail.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MovieLikeMapper {

    // 영화 좋아요 등록
    void insertLike(@Param("movieId") Integer movieId, @Param("memId") Integer memId);

    // 영화 좋아요 취소
    void deleteLike(@Param("movieId") Integer movieId, @Param("memId") Integer memId);

    // 영화 좋아요 여부 확인
    int isMovieLiked(@Param("movieId") Integer movieId, @Param("memId") Integer memId);

    // 영화 좋아요 개수 조회
    int getLikeCount(@Param("movieId") Integer movieId);
}
