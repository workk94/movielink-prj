package com.acorn.movielink.login.repository;

import com.acorn.movielink.login.dto.Movie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MovieMapper {


    Movie findByMovieId(@Param("movieId") Integer movieId);
    
    List<Movie> findBestMovies();
}
