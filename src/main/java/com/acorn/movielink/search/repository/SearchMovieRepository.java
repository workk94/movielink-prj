package com.acorn.movielink.search.repository;

import com.acorn.movielink.search.dto.SearchMovieDTO;
import com.acorn.movielink.search.dto.SearchPeopleDTO;
import com.acorn.movielink.search.dto.SearchPostDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SearchMovieRepository {

    @Autowired
    private SqlSession session;

    private final String namespace = "com.acorn.movielink.SearchMapper.";


    public List<SearchMovieDTO> findMovies(String keyword, int offset, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("offset", offset);
        params.put("limit", limit);
        return session.selectList(namespace + "findMovies", params);
    }


    public int getMovieCount(String keyword) {
        return session.selectOne(namespace + "getMovieCount", keyword);
    }


    public List<SearchPeopleDTO> findPeople(String keyword, int offset, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("offset", offset);
        params.put("limit", limit);
        return session.selectList(namespace + "findPeople", params);
    }


    public int getPeopleCount(String keyword) {
        return session.selectOne(namespace + "getPeopleCount", keyword);
    }


    public List<SearchPostDTO> findPosts(String keyword, int offset, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("offset", offset);
        params.put("limit", limit);
        return session.selectList(namespace + "findPosts", params);
    }


    public int getPostCount(String keyword) {
        return session.selectOne(namespace + "getPostCount", keyword);
    }
}