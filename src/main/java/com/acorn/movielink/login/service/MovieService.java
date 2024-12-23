package com.acorn.movielink.login.service;

import com.acorn.movielink.MovieMapper;
import com.acorn.movielink.data.dto.MovieDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieMapper movieMapper;

    public MovieService(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    public List<MovieDTO> getTopBestMovies(int limit) {
        List<MovieDTO> bestMovies = movieMapper.findBestMovies();
        return bestMovies.stream().limit(limit).collect(Collectors.toList());
    }

    public List<MovieDTO> getFilmographyByPeopleId(Integer peopleId) {
        return movieMapper.selectMoviesByPeopleId(peopleId);
    }
}
