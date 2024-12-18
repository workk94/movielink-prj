package com.acorn.movielink.data.service;

import com.acorn.movielink.data.dto.MovieDTO;

import java.util.List;

public interface MovieService {

    // 영화 저장
    int saveMovie();

    // 영화 전체 조회
    List<MovieDTO> getMovieList();
}
