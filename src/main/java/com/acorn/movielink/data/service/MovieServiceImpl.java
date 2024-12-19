package com.acorn.movielink.data.service;

import com.acorn.movielink.data.dto.MovieInfoDTO;
import com.acorn.movielink.data.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl  implements  MovieService{

    @Autowired
    MovieRepository movieRepository;

    @Override
    public List<MovieInfoDTO> getAllMovie() {
        return movieRepository.selectAllMovie();
    }
}
