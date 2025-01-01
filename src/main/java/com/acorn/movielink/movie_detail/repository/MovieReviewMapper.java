package com.acorn.movielink.movie_detail.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MovieReviewMapper {
    // 리뷰 작성 여부 확인
    int isMovieReviewed(@Param("movieId") Integer movieId, @Param("memId") Integer memId);

    // 리뷰 등록
    void insertReview(@Param("movieId") Integer movieId,
                      @Param("memId") Integer memId,
                      @Param("reviewTitle") String reviewTitle,
                      @Param("reviewContent") String reviewContent,
                      @Param("reviewRating") double reviewRating);

    // 리뷰 수정
    void updateReview(@Param("reviewId") Integer reviewId,
                      @Param("reviewContent") String reviewContent,
                      @Param("reviewRating") double reviewRating);

    // 리뷰 삭제
    void deleteReview(@Param("reviewId") Integer reviewId);

    // 리뷰 리스트 조회 (영화별)
    List<Map<String, Object>> getReview(@Param("movieId") Integer movieId);
}
