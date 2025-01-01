package com.acorn.movielink.movie_detail.repository;

import com.acorn.movielink.movie_detail.dto.MovieReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MovieReviewMapper {
    // 리뷰 작성 여부 확인
    int isMovieReviewed(@Param("movieId") Integer movieId,
                        @Param("memId") Integer memId);

    // 리뷰 등록
    void insertReview(@Param("movieId") Integer movieId,
                      @Param("memId") Integer memId,
                      @Param("reviewContent") String reviewContent,
                      @Param("reviewRating") double reviewRating);

    // 리뷰 수정
    void updateReview(@Param("reviewId") Integer reviewId,
                      @Param("reviewContent") String reviewContent,
                      @Param("reviewRating") double reviewRating);

    // 리뷰 삭제
    void deleteReview(@Param("reviewId") Integer reviewId);

    // 특정 영화의 모든 리뷰 조회
    List<MovieReview> getReview(@Param("movieId") Integer movieId);

    // 특정 사용자의 리뷰 조회
    MovieReview getUserReview(@Param("movieId") Integer movieId, @Param("memId") Integer memId);
}
