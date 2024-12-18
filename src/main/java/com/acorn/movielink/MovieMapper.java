package com.acorn.movielink;

import com.acorn.movielink.data.dto.MovieDTO;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MovieMapper {
    
    List<MovieDTO> findBestMovies();
}
