package com.acorn.movielink.search.repository;


import com.acorn.movielink.search.dto.SearchMovieDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SearchMovieRepository {

    @Autowired
    private SqlSession session;

    private String namespace = " ";

    public List<SearchMovieDTO> findMovieByMovieNm (String movieNm){
        return session.selectList(namespace + "SearchMovieNm");
    }
}
