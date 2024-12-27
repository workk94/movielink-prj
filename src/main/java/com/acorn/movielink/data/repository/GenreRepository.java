package com.acorn.movielink.data.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class GenreRepository {

    private final SqlSession session;
    private static final String namespace = "com.acorn.movielink.GenreMapper.";

    @Autowired
    public GenreRepository(SqlSession session){
        this.session = session;
    }

    // 장르 삽입
    public int insert(String genreNm, int movieId){
        Map<String, Object> params = new HashMap<>();
        params.put("genreName", genreNm);
        params.put("movieId", movieId);
        return session.insert(namespace + "insert", params);
    };
}
