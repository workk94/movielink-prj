package com.acorn.movielink.tier.repository;

import com.acorn.movielink.tier.dto.TierMovieDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TierMovieMapper {
    List<TierMovieDTO> findMoviesByFilter(
            @Param("startYear") Integer startYear,
            
            @Param("genreId") Integer genreId
    );
}
