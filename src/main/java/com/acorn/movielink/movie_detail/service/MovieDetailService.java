package com.acorn.movielink.movie_detail.service;

import com.acorn.movielink.data.dto.MovieDTO;

import java.util.List;
import java.util.Map;

public interface MovieDetailService {
    MovieDTO getMovieById(Integer movieId); // 영화 상세 정보
    List<Map<String, Object>> getPeopleById(Integer movieId, String peopleType);
}
