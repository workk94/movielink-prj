package com.acorn.movielink.data.service;

import com.acorn.movielink.data.dto.MovieDailyStatsDTO;
import com.acorn.movielink.data.repository.MovieBoxOfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieDailyBoxOfficeServiceImpl implements MovieDailyBoxOfficeService{

    @Autowired
    MovieBoxOfficeRepository movieBoxOfficeRepository;

    @Override
    public List<MovieDailyStatsDTO> getDailyStats(String boxOfficeDate) {
        return  movieBoxOfficeRepository.findDailyMovieStats(boxOfficeDate);
    }
}