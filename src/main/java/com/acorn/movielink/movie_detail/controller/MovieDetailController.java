package com.acorn.movielink.movie_detail.controller;

import com.acorn.movielink.data.dto.MovieDTO;
import com.acorn.movielink.login.dto.Member;
import com.acorn.movielink.login.service.MemberService;
import com.acorn.movielink.movie_detail.service.MovieDetailServiceImpl;
import com.acorn.movielink.movie_detail.service.MovieLikeServiceImpl;
import com.acorn.movielink.movie_detail.service.MovieReviewServiceImpl;
import com.acorn.movielink.people_detail.dto.Post;
import com.acorn.movielink.people_detail.service.PostService;
import com.acorn.movielink.movie_detail.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/title")
public class MovieDetailController {

    @Autowired
    private MovieDetailServiceImpl movieDetailService;

    @Autowired
    private MovieLikeServiceImpl movieLikeService;

    @Autowired
    private PostService postService;

    @Autowired
    private MovieReviewServiceImpl movieReviewService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserService userService;

    @GetMapping("/{movieId}")
    public String fetchMovieDetails(
            @PathVariable("movieId") Integer movieId,
            @RequestParam(value = "tab", required = false, defaultValue = "mvinfo") String tab,
            Authentication authentication,
            Model model) {

        MovieDTO movie = movieDetailService.getMovieById(movieId);
        model.addAttribute("movie", movie);
        model.addAttribute("movieId", movieId);
        model.addAttribute("tab", tab);

        List<Map<String, Object>> actors = movieDetailService.getPeopleById(movieId, "actor");
        List<Map<String, Object>> directors = movieDetailService.getPeopleById(movieId, "director");
        List<Map<String, Object>> staffs = movieDetailService.getPeopleById(movieId, "staff");

        model.addAttribute("directors", directors);
        model.addAttribute("actors", actors);
        model.addAttribute("staffs", staffs);

        // 리뷰 데이터 추가
        List<Map<String, Object>> review = movieReviewService.getReview(movieId);
        model.addAttribute("reviews", review);

        // 로그인 여부 확인
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated();
        model.addAttribute("isLoggedIn", isLoggedIn);

        // 로그인한 사용자의 리뷰 확인
        boolean reviewExists = false;
        if (isLoggedIn) {
            String email = userService.getUserEmailFromPrincipal(authentication);
            if (email != null) {
                Optional<Member> memberOpt = memberService.findByEmail(email);
                if (memberOpt.isPresent()) {
                    Integer memId = memberOpt.get().getMemId();
                    reviewExists = movieReviewService.isMovieReviewed(movieId, memId);
                }
            }
        }
        model.addAttribute("reviewExists", reviewExists);


        if (movie != null) {
            String tagName = movie.getMovieNm();
            List<Post> communityPosts = postService.getPostsByTagName(tagName);
            model.addAttribute("communityPosts", communityPosts);
        }

        return "MovieDetail"; // MovieDetail.html
    }

    @PostMapping("/{movieId}/review")
    @ResponseBody
    public ResponseEntity<?> addOrEditReview(
            @PathVariable("movieId") Integer movieId,
            @RequestBody Map<String, Object> reviewData,
            Authentication authentication,
            HttpServletRequest request) {

        // 로그인 여부 확인
        if (authentication == null || !authentication.isAuthenticated()) {
            // 현재 요청 URL을 세션에 저장
            String currentUrl = request.getRequestURI() + "?" + request.getQueryString();
            request.getSession().setAttribute("redirectUrl", currentUrl);

            // 로그인 페이지로 이동하도록 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("/login");
        }

        try {
            String reviewTitle = (String) reviewData.get("reviewTitle");
            String reviewContent = (String) reviewData.get("reviewContent");
            double reviewRating = (double) reviewData.get("reviewRating");

            // 사용자 ID 가져오기
            String email = authentication.getName();
            Optional<Member> memberOpt = memberService.findByEmail(email);
            if (memberOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보를 찾을 수 없습니다.");
            }

            Integer memId = memberOpt.get().getMemId();
            Integer reviewId = reviewData.containsKey("reviewId") ? (Integer) reviewData.get("reviewId") : null;

            if (reviewId == null) {
                // 리뷰 저장 (새 리뷰 작성)
                movieReviewService.addReview(movieId, reviewTitle, reviewContent, reviewRating, authentication);
                return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");
            } else {
                // 기존 리뷰 수정
                movieReviewService.updateReview(reviewId, reviewContent, reviewRating);
                return ResponseEntity.ok("리뷰가 성공적으로 수정되었습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 처리 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/{movieId}/review/delete")
    @ResponseBody
    public ResponseEntity<?> deleteReview(
            @PathVariable("movieId") Integer movieId,
            @RequestBody Map<String, Object> reviewData) {
        try {
            Integer reviewId = (Integer) reviewData.get("reviewId");
    
            movieReviewService.deleteReview(reviewId);
            return ResponseEntity.ok("리뷰가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 삭제 중 오류가 발생했습니다.");
        }
    }    

    @PostMapping("/{movieId}/like")
    @ResponseBody
    public ResponseEntity<?> likeMovie(
            @PathVariable("movieId") Integer movieId,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String email = userService.getUserEmailFromPrincipal(authentication);
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Optional<Member> memberOpt = memberService.findByEmail(email);
        if (memberOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원 정보를 찾을 수 없습니다.");
        }

        Integer memId = memberOpt.get().getMemId();
        boolean alreadyLiked = movieLikeService.isMovieLikedByUser(movieId, memId);

        if (alreadyLiked) {
            movieLikeService.unlikeMovie(movieId, memId);
            int likeCount = movieLikeService.getLikeCount(movieId);
            return ResponseEntity.ok("좋아요가 해제되었어요!");
        }

        movieLikeService.likeMovie(movieId, memId);
        int likeCount = movieLikeService.getLikeCount(movieId);

        return ResponseEntity.ok(likeCount);
    }

    @DeleteMapping("/{movieId}/like")
    @ResponseBody
    public ResponseEntity<?> unlikeMovie(
            @PathVariable("movieId") Integer movieId,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String email = userService.getUserEmailFromPrincipal(authentication);
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Optional<Member> memberOpt = memberService.findByEmail(email);
        if (memberOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원 정보를 찾을 수 없습니다.");
        }

        Integer memId = memberOpt.get().getMemId();
        boolean alreadyLiked = movieLikeService.isMovieLikedByUser(movieId, memId);

        if (!alreadyLiked) {
            return ResponseEntity.badRequest().body("좋아요를 하지 않았습니다.");
        }

        movieLikeService.unlikeMovie(movieId, memId);
        int likeCount = movieLikeService.getLikeCount(movieId);

        return ResponseEntity.ok(likeCount);
    }
}
