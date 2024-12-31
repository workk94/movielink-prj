package com.acorn.movielink;

import com.acorn.movielink.data.dto.MovieDTO;
import com.acorn.movielink.people_detail.dto.People;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MovieMapper {

    List<MovieDTO> findBestMovies();

    List<MovieDTO> selectMoviesByPeopleId(Integer peopleId);

    List<MovieDTO> findMoviesByOpenDateDesc();
}
