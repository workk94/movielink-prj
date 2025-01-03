package com.acorn.movielink.movie_detail.controller;

import com.acorn.movielink.data.dto.MovieDTO;
import com.acorn.movielink.login.service.MemberService;
import com.acorn.movielink.movie_detail.dto.MovieReview;
import com.acorn.movielink.movie_detail.service.MovieDetailServiceImpl;
import com.acorn.movielink.movie_detail.service.MovieLikeServiceImpl;
import com.acorn.movielink.movie_detail.service.MovieReviewServiceImpl;
import com.acorn.movielink.people_detail.dto.Post;
import com.acorn.movielink.people_detail.dto.Tag;
import com.acorn.movielink.people_detail.service.PostService;
import com.acorn.movielink.movie_detail.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

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

        // 영화 정보 가져오기
        MovieDTO movie = movieDetailService.getMovieById(movieId);
        model.addAttribute("movie", movie);
        model.addAttribute("movieId", movieId);
        model.addAttribute("tab", tab);

        List<Map<String, Object>> actors = movieDetailService.getPeopleById(movieId, "배우");
        List<Map<String, Object>> directors = movieDetailService.getPeopleById(movieId, "감독");
        List<Map<String, Object>> staffs = movieDetailService.getPeopleById(movieId, "스텝");

        model.addAttribute("directors", directors);
        model.addAttribute("actors", actors);
        model.addAttribute("staffs", staffs);

        // 로그인 여부 및 사용자 정보 확인
        Integer memId = userService.getMemberIdFromAuthentication(authentication, memberService);
        boolean isLoggedIn = memId != null;
        model.addAttribute("isLoggedIn", isLoggedIn);

        // 사용자 리뷰 확인
        boolean reviewExists = isLoggedIn && movieReviewService.isMovieReviewed(movieId, memId);
        model.addAttribute("reviewExists", reviewExists);

        if (memId != null) {
            MovieReview userReview = movieReviewService.getUserReview(movieId, memId);
            model.addAttribute("userReview", userReview);
        }

        List<MovieReview> reviews = movieReviewService.getReview(movieId);
        model.addAttribute("reviews", reviews);

        // 태그 및 커뮤니티 게시글 조회
        String tagName = movie.getMovieNm(); // 영화 이름을 태그로 사용
        List<Post> communityPosts = postService.getPostsByTagName(tagName);
        model.addAttribute("communityPosts", communityPosts);

        // 영화 좋아요 상태 확인
        boolean isLiked = isLoggedIn && movieLikeService.isMovieLikedByUser(movieId, memId);
        model.addAttribute("isLiked", isLiked);

        return "MovieDetail";
    }

    @PostMapping("/{movieId}/review/register")
    @ResponseBody
    public ResponseEntity<?> addReview(
            @PathVariable("movieId") Integer movieId,
            @RequestBody Map<String, Object> reviewData,
            Authentication authentication) {

        // 로그인 여부 확인
        Integer memId = userService.getMemberIdFromAuthentication(authentication, memberService);
        if (memId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            String reviewContent = (String) reviewData.get("reviewContent");
            double reviewRating = (double) reviewData.get("reviewRating");

            // 리뷰 저장 서비스 호출
            movieReviewService.addReview(movieId, reviewContent, reviewRating, authentication);
            return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 작성 중 오류가 발생했습니다.");
        }
    }

    @PutMapping("/{movieId}/{reviewId}/edit")
    @ResponseBody
    public ResponseEntity<?> editReview(
            @PathVariable("movieId") Integer movieId,
            @PathVariable("reviewId") Integer reviewId,
            @RequestBody Map<String, Object> reviewData,
            Authentication authentication) {

        Integer memId = userService.getMemberIdFromAuthentication(authentication, memberService);
        if (memId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            String reviewContent = (String) reviewData.get("reviewContent");
            double reviewRating = (double) reviewData.get("reviewRating");

            movieReviewService.updateReview(reviewId, reviewContent, reviewRating);
            return ResponseEntity.ok("리뷰가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 수정 중 오류가 발생했습니다.");
        }
    }

    @DeleteMapping("/{movieId}/{reviewId}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteReview(
            @PathVariable("movieId") Integer movieId,
            @PathVariable("reviewId") Integer reviewId,
            Authentication authentication) {

        Integer memId = userService.getMemberIdFromAuthentication(authentication, memberService);
        if (memId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
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

        Integer memId = userService.getMemberIdFromAuthentication(authentication, memberService);
        if (memId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        boolean alreadyLiked = movieLikeService.isMovieLikedByUser(movieId, memId);

        if (alreadyLiked) {
            return ResponseEntity.badRequest().body("이미 좋아요를 눌렀습니다.");
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

        Integer memId = userService.getMemberIdFromAuthentication(authentication, memberService);
        if (memId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        boolean alreadyLiked = movieLikeService.isMovieLikedByUser(movieId, memId);

        if (!alreadyLiked) {
            return ResponseEntity.badRequest().body("좋아요를 누르지 않았습니다.");
        }

        movieLikeService.unlikeMovie(movieId, memId);
        int likeCount = movieLikeService.getLikeCount(movieId);
        return ResponseEntity.ok(likeCount);
    }
}
