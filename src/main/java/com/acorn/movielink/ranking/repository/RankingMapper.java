package com.acorn.movielink.ranking.repository;

import com.acorn.movielink.people_detail.dto.People;
import com.acorn.movielink.ranking.dto.RankingDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RankingMapper {

    List<RankingDTO> selectMovieByRank();
}
