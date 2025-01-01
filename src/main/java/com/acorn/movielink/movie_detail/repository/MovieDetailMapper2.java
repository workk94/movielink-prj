package com.acorn.movielink.movie_detail.repository;

import com.acorn.movielink.movie_detail.dto.MovieDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MovieDetailMapper2 {
    MovieDTO getMovieDetail(int movieId);
}
