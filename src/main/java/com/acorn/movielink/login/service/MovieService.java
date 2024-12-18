package com.acorn.movielink.login.service;

import com.acorn.movielink.login.dto.Movie;
import com.acorn.movielink.login.repository.MovieMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieMapper movieMapper;

    public MovieService(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    public List<Movie> getTopBestMovies(int limit) {
        List<Movie> bestMovies = movieMapper.findBestMovies();
        return bestMovies.stream().limit(limit).collect(Collectors.toList());
    }
}
