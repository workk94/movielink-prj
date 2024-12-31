package com.acorn.movielink.movie_detail.service;

import com.acorn.movielink.login.dto.Member;
import com.acorn.movielink.login.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MovieReviewServiceImpl {

    @Autowired
    private MovieReviewService movieReviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private MemberService memberService;


    // 리뷰 작성 여부 확인 (memId를 직접 받도록 수정)
    public boolean isMovieReviewed(Integer movieId, Integer memId) {
        if (memId == null) {
            return false; // 사용자 ID가 없는 경우
        }
        return movieReviewService.isMovieReviewed(movieId, memId);
    }

    // 리뷰 등록
    public String addReview(Integer movieId, String reviewTitle, String reviewContent, double reviewRating, Authentication authentication) {
        String email = userService.getUserEmailFromPrincipal(authentication);
        if (email == null) {
            return "로그인이 필요합니다."; // 로그인되지 않은 경우
        }

        Optional<Member> memberOpt = memberService.findByEmail(email);
        if (memberOpt.isEmpty()) {
            return "회원 정보를 찾을 수 없습니다."; // 회원 정보를 찾을 수 없는 경우
        }

        Integer memId = memberOpt.get().getMemId();
        movieReviewService.insertReview(movieId, memId, reviewTitle, reviewContent, reviewRating);
        return "리뷰 등록 완료";
    }

    // 리뷰 수정
    public String updateReview(Integer reviewId, String reviewContent, double reviewRating) {
        movieReviewService.updateReview(reviewId, reviewContent, reviewRating);
        return "리뷰 수정 완료";
    }

    // 리뷰 삭제
    public String deleteReview(Integer reviewId) {
        movieReviewService.deleteReview(reviewId);
        return "리뷰 삭제 완료";
    }

    // 특정 영화의 모든 리뷰 조회
    public List<Map<String, Object>> getReview(Integer movieId) {
        return movieReviewService.getReview(movieId);
    }
}