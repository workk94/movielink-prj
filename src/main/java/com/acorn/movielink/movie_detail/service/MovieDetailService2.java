package com.acorn.movielink.movie_detail.service;

import com.acorn.movielink.movie_detail.dto.MovieDTO;
import com.acorn.movielink.movie_detail.repository.MovieDetailMapper2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovieDetailService2 {
    @Autowired
    private MovieDetailMapper2 movieDetailMapper2;

    public MovieDTO getMovieDetail(int movieId) {
        return movieDetailMapper2.getMovieDetail(movieId);
    }
}
