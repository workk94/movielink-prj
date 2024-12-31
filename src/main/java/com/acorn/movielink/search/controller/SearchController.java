package com.acorn.movielink.search.controller;

import com.acorn.movielink.data.dto.MovieDTO;
import com.acorn.movielink.login.service.MovieService;
import com.acorn.movielink.search.dto.SearchMovieDTO;
import com.acorn.movielink.search.dto.SearchPeopleDTO;
import com.acorn.movielink.search.dto.SearchPostDTO;
import com.acorn.movielink.search.service.SearchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/search")
public class SearchController {

    private SearchServiceImpl searchService;
    private MovieService movieService;


    @Autowired
    public SearchController(SearchServiceImpl searchService, MovieService movieService) {
        this.searchService = searchService;
        this.movieService = movieService;
    }

    @GetMapping
    public String searchPage(Model model) {

        // 추천영화 10개 반환
        List<MovieDTO> recommendedMovies = movieService.getLatestMoviesByOpenDateDesc();
        model.addAttribute("recommendedMovies", recommendedMovies);

        return "search";
    }

    @GetMapping("/results")
    public ResponseEntity<?> searchResults(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "limit", defaultValue = "4") int limit
    ) {
        List<SearchMovieDTO> movies = searchService.searchMovie(keyword, offset, limit);
        List<SearchPeopleDTO> people = searchService.searchPeople(keyword, offset, limit);
        List<SearchPostDTO> posts = searchService.searchPosts(keyword, offset, limit);

        return ResponseEntity.ok(Map.of(
                "movies", Map.of("results", movies, "totalCount", searchService.getMovieCount(keyword)),
                "people", Map.of("results", people, "totalCount", searchService.getPeopleCount(keyword)),
                "posts", Map.of("results", posts, "totalCount", searchService.getPostCount(keyword))
        ));
    }


}
