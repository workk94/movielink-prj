package com.acorn.movielink.data.repository;

import com.acorn.movielink.data.dto.MovieDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MovieRepository {

    @Autowired
    SqlSession session;


    public List<MovieDTO> selectAllMovie(){
        return session.selectList("com.acorn.movielink.data.MovieMapper.selectAllMovies");
    }

}
