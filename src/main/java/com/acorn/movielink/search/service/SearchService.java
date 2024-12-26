package com.acorn.movielink.search.service;

import com.acorn.movielink.search.dto.SearchMovieDTO;
import com.acorn.movielink.search.dto.SearchPeopleDTO;
import com.acorn.movielink.search.dto.SearchPostDTO;

import java.util.List;

public interface SearchService {

    // 영화 이름으로 검색
    List<SearchMovieDTO> searchMovie(String movieNm, int start, int end);

    // 영화 검색 결과 카운트
    int getMovieCount(String keyword);

    // 영화인 이름으로 검색
    List<SearchPeopleDTO> searchPeople(String keyword, int offset, int limit);

    // 영화인 검색 결과 카운트
    int getPeopleCount(String keyword);

    // 게시물 제목 && 내용으로 검색
    List<SearchPostDTO> searchPosts(String keyword, int offset, int limit);

    // 게시물 검색 결과 카운트
    int getPostCount(String keyword);

}
