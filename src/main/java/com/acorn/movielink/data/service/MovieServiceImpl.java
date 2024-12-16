package com.acorn.movielink.data.service;

import com.acorn.movielink.data.dto.MovieDTO;
import com.acorn.movielink.data.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MovieServiceImpl  implements  MovieService{

    @Autowired
    MovieRepository movieRepository;

    @Override
    public List<MovieDTO> getAllMovie() {
        return movieRepository.selectAllMovie();
    }
}
