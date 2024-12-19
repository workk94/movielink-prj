package com.acorn.movielink.data.repository;

import com.acorn.movielink.data.dto.MovieDailyStatsDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MovieBoxOfficeRepository {

    @Autowired
    SqlSession session;

    public List<MovieDailyStatsDTO> findDailyMovieStats(@Param("boxOfficeDate") String boxOfficeDate){
        return session.selectList("com.acorn.movielink.data.MovieBoxOfficeMapper.findDailyMovieStats",boxOfficeDate);
    }

}
