package com.acorn.movielink.data.service;

import com.acorn.movielink.data.dto.MovieDTO;

import java.util.List;
import java.util.Map;

public interface MovieService {

    // 영화 저장
    int saveMovie();

    // 영화 전체 조회
    List<MovieDTO> getMovieList();

    // 존재하는 영화 조회
    List<Map<String, String>> getExistingMovie();

}
