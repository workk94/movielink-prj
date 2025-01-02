package com.acorn.movielink.movie_detail.service;

import com.acorn.movielink.login.dto.Member;
import com.acorn.movielink.login.service.MemberService;
import com.acorn.movielink.movie_detail.dto.MovieReview;
import com.acorn.movielink.movie_detail.repository.MovieReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieReviewServiceImpl {

    @Autowired
    private MovieReviewService movieReviewService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserService userService;

    @Autowired
    private MovieReviewMapper movieReviewMapper;


    /**
     * 리뷰 작성 여부 확인
     *
     * @param movieId 영화 ID
     * @param memId   회원 ID
     * @return 작성 여부 (true/false)
     */
    public boolean isMovieReviewed(Integer movieId, Integer memId) {
        if (memId == null) {
            return false; // 사용자 ID가 없는 경우
        }
        return movieReviewService.isMovieReviewed(movieId, memId);
    }

    /**
     * 리뷰 등록
     *
     * @param movieId       영화 ID
     * @param reviewContent 리뷰 내용
     * @param reviewRating  리뷰 평점
     * @param authentication 인증 객체
     * @return 처리 결과 메시지
     */
    public String addReview(Integer movieId, String reviewContent, double reviewRating, Authentication authentication) {
        String email = userService.getUserEmailFromPrincipal(authentication);
        if (email == null) {
            return "로그인이 필요합니다."; // 로그인되지 않은 경우
        }

        Optional<Member> memberOpt = memberService.findByEmail(email);
        if (memberOpt.isEmpty()) {
            return "회원 정보를 찾을 수 없습니다."; // 회원 정보를 찾을 수 없는 경우
        }

        Integer memId = memberOpt.get().getMemId();

        if (isMovieReviewed(movieId, memId)) {
            return "이미 리뷰를 작성하셨습니다."; // 이미 리뷰 작성된 경우
        }

        movieReviewService.insertReview(movieId, memId, reviewContent, reviewRating);
        return "리뷰가 성공적으로 등록되었습니다.";
    }

    /**
     * 리뷰 수정
     * @param reviewId      리뷰 ID
     * @param reviewContent 리뷰 내용
     * @param reviewRating  리뷰 평점
     * @return 처리 결과 메시지
     */
    public void updateReview(Integer reviewId, String reviewContent, double reviewRating) {

        if (reviewId == null) {
            throw new IllegalArgumentException("리뷰 ID가 null입니다.");
        }

        System.out.println("service");
        System.out.println(reviewId);
        System.out.println(reviewContent);
        System.out.println(reviewRating);

        movieReviewMapper.updateReview(reviewId, reviewContent, reviewRating);

    }


    /**
     * 리뷰 삭제
     *
     * @param reviewId 리뷰 ID
     * @return 처리 결과 메시지
     */
    public String deleteReview(Integer reviewId) {
        movieReviewService.deleteReview(reviewId);
        return "리뷰가 성공적으로 삭제되었습니다.";
    }

    /**
     * 특정 영화의 모든 리뷰 조회
     *
     * @param movieId 영화 ID
     * @return 리뷰 리스트
     */
    public List<MovieReview> getReview(Integer movieId) {
        return movieReviewService.getReview(movieId);
    }

    /**
     * 특정 사용자의 리뷰 조회
     *
     * @param movieId 영화 ID
     * @param memId   회원 ID
     * @return 특정 사용자의 리뷰 데이터
     */
    public MovieReview getUserReview(Integer movieId, Integer memId) {
        if (memId == null) {
            return null; // 사용자 ID가 없는 경우
        }
        return movieReviewService.getUserReview(movieId, memId);
    }
}
