package com.acorn.movielink.main.controller;

import com.acorn.movielink.data.dto.MovieDTO;
import com.acorn.movielink.login.dto.Member;
<<<<<<< HEAD
import com.acorn.movielink.login.dto.Movie;
=======
>>>>>>> 9a4aaace81ba518684f9f7989c50f66c86418793
import com.acorn.movielink.login.dto.Notice;
import com.acorn.movielink.login.dto.Review;
import com.acorn.movielink.login.service.MemberService;
import com.acorn.movielink.login.service.MovieService;
import com.acorn.movielink.login.service.NoticeService;
import com.acorn.movielink.login.service.ReviewService;
<<<<<<< HEAD
import com.acorn.movielink.youtube.YouTubeService;
=======
import com.acorn.movielink.youtube.service.YouTubeService;
>>>>>>> 9a4aaace81ba518684f9f7989c50f66c86418793
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {

    private final MemberService memberService;
    private final ReviewService reviewService;
    private final MovieService movieService;
    private final NoticeService noticeService;
<<<<<<< HEAD
//실시간 동접자
=======
    //실시간 동접자
>>>>>>> 9a4aaace81ba518684f9f7989c50f66c86418793
//    @Autowired
//    private ActiveUserService activeUserService;
    private final YouTubeService youTubeService;

    @Autowired
    public MainController(MemberService memberService, ReviewService reviewService, MovieService movieService, YouTubeService youTubeService, NoticeService noticeService) {
        this.memberService = memberService;
        this.reviewService = reviewService;
        this.movieService = movieService;
        this.youTubeService = youTubeService;
        this.noticeService = noticeService;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        // 최신 리뷰 10개 조회
        List<Review> latestReviews = reviewService.getLatestReviews();
        model.addAttribute("latestReviews", latestReviews);

        // 베스트 무비 상위 3개 조회
        List<MovieDTO> bestMovies = movieService.getTopBestMovies(3);
        model.addAttribute("bestMovies", bestMovies);

        List<String> youtubeost = youTubeService.searchMovieMusicVideos("ost");
        model.addAttribute("youtubeost", youtubeost);

        // 현재 로그인한 사용자 ID 가져오기
        Integer memId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            memId = memberService.findByEmail(email)
                    .map(Member::getMemId)
                    .orElse(null);
        }
<<<<<<< HEAD
        List<Movie> recommendedMovies;
=======
        List<MovieDTO> recommendedMovies;
>>>>>>> 9a4aaace81ba518684f9f7989c50f66c86418793
        if (memId != null) {
            // 추천 영화 10개 조회
            recommendedMovies = memberService.getRecommendedMovies(memId);
            model.addAttribute("recommendedMovies", recommendedMovies);
        } else {
            List<MovieDTO> latestMovies = movieService.getLatestMoviesByOpenDateDesc();
            recommendedMovies = new ArrayList<>();
            for (MovieDTO dto : latestMovies) {
<<<<<<< HEAD
                Movie movie = new Movie();
                // DTO 필드 → Movie 필드
                movie.setMovieId(dto.getMovieId());
                movie.setMovieNm(dto.getMovieNm());
                movie.setMoviePosterUrl(dto.getMoviePosterUrl());

                recommendedMovies.add(movie);
=======
                MovieDTO moviedto = new MovieDTO();
                // DTO 필드 → Movie 필드
                moviedto.setMovieId(dto.getMovieId());
                moviedto.setMovieNm(dto.getMovieNm());
                moviedto.setMoviePosterUrl(dto.getMoviePosterUrl());

                recommendedMovies.add(moviedto);
>>>>>>> 9a4aaace81ba518684f9f7989c50f66c86418793
            }
        }
        model.addAttribute("recommendedMovies", recommendedMovies);
        //실시간 동접자
        //int activeUserCount = activeUserService.getActiveUserCount();
        //model.addAttribute("activeUserCount", activeUserCount);

        // 최신 공지 가져오기
        Optional<Notice> latestNoticeOpt = noticeService.getLatestNotice();
        if (latestNoticeOpt.isPresent()) {
            model.addAttribute("latestNotice", latestNoticeOpt.get());
        } else {
            model.addAttribute("latestNotice", null);
        }

        return "main";
    }
//실시간 동접자
//    @GetMapping("/active-user-count")
//    @ResponseBody
//    public Map<String, Integer> getActiveUserCount() {
//        int activeUserCount = activeUserService.getActiveUserCount();
//        Map<String, Integer> response = new HashMap<>();
//        response.put("activeUserCount", activeUserCount);
//        return response;
//    }


}
