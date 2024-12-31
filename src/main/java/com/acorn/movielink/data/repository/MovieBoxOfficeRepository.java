package com.acorn.movielink.data.repository;

import com.acorn.movielink.data.dto.BoxOfficeMovieDTO;
import com.acorn.movielink.data.dto.MovieDailyStatsDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MovieBoxOfficeRepository {

    @Autowired
    SqlSession session;

    public List<MovieDailyStatsDTO> findDailyMovieStats(@Param("boxOfficeDate") String boxOfficeDate){
        return session.selectList("com.acorn.movielink.data.MovieBoxOfficeMapper.findDailyMovieStats",boxOfficeDate);
    }

    public  BoxOfficeMovieDTO findBoxOfficeMovies(@Param("movie_nm") String movie_nm, @Param("movie_open_dt")  String movie_open_dt){
        Map<String,Object> params = new HashMap<>();
        params.put("movie_nm",movie_nm);
        params.put("movie_open_dt",movie_open_dt);

        return session.selectOne("com.acorn.movielink.data.MovieBoxOfficeMapper.findBoxofficeMovies",params);
    }

}
