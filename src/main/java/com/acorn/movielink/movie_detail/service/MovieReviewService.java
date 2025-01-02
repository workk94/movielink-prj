package com.acorn.movielink.movie_detail.service;

import com.acorn.movielink.movie_detail.dto.MovieReview;
import com.acorn.movielink.movie_detail.repository.MovieReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieReviewService {

    @Autowired
    private MovieReviewMapper movieReviewMapper;

    // 리뷰 작성 여부 확인
    public boolean isMovieReviewed(Integer movieId, Integer memId) {
        return movieReviewMapper.isMovieReviewed(movieId, memId) > 0;
    }

    // 리뷰 등록
    public void insertReview(Integer movieId, Integer memId, String reviewContent, double reviewRating) {
        movieReviewMapper.insertReview(movieId, memId, reviewContent, reviewRating);
    }

    // 리뷰 수정
    public void updateReview(Integer reviewId, String reviewContent, double reviewRating) {
        if (reviewId == null) {
            throw new IllegalArgumentException("리뷰 ID가 null입니다.");
        }

        movieReviewMapper.updateReview(reviewId, reviewContent, reviewRating);
    }


    // 리뷰 삭제
    public void deleteReview(Integer reviewId) {
        movieReviewMapper.deleteReview(reviewId);
    }

    // 특정 영화의 모든 리뷰 조회
    public List<MovieReview> getReview(Integer movieId) {
        return movieReviewMapper.getReview(movieId);
    }

    // 특정 사용자의 리뷰 조회
    public MovieReview getUserReview(Integer movieId, Integer memId) {
        return movieReviewMapper.getUserReview(movieId, memId);
    }
}
