package com.acorn.movielink.data.service;

import com.acorn.movielink.data.dto.BoxOfficeMovieDTO;
import com.acorn.movielink.data.dto.MovieDailyStatsDTO;

import java.util.List;


public interface MovieDailyBoxOfficeService {
    List<MovieDailyStatsDTO> getDailyStats(String boxOfficeDate);
    BoxOfficeMovieDTO getBoxOfficeMovies(String movie_nm, String open_dt);
}
