package com.acorn.movielink;

import com.acorn.movielink.data.dto.MovieDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MovieMapper {

    List<MovieDTO> findBestMovies();

    List<MovieDTO> selectMoviesByPeopleId(Integer peopleId);

    List<MovieDTO> findMoviesByOpenDateDesc();
}
