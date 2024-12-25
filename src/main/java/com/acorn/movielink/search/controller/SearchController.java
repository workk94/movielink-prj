package com.acorn.movielink.search.controller;

import com.acorn.movielink.search.dto.SearchMovieDTO;
import com.acorn.movielink.search.dto.SearchPeopleDTO;
import com.acorn.movielink.search.dto.SearchPostDTO;
import com.acorn.movielink.search.service.SearchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchServiceImpl searchService;

    @GetMapping
    public String searchPage() {
        return "search";
    }

    @GetMapping("/results")
    public ResponseEntity<?> searchResults(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "limit", defaultValue = "4") int limit
    ) {
        // 2글자 미만 검색어 처리
        if (keyword.length() < 2) {
            return ResponseEntity.badRequest().body(Map.of("error", "검색어는 최소 2글자 이상 입력해야 합니다."));
        }

        // 기존 로직 유지
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
