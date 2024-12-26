package com.acorn.movielink.search.service;

import com.acorn.movielink.search.dto.SearchMovieDTO;
import com.acorn.movielink.search.dto.SearchPeopleDTO;
import com.acorn.movielink.search.dto.SearchPostDTO;
import com.acorn.movielink.search.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService{

    @Autowired
    private SearchMovieRepository repository;

    @Override
    public List<SearchMovieDTO> searchMovie(String keyword, int offset, int limit) {
        return repository.findMovies(keyword, offset, limit);
    }

    @Override
    public int getMovieCount(String keyword) {
        return repository.getMovieCount(keyword);
    }

    // 영화인 url 수정
    @Override
    public List<SearchPeopleDTO> searchPeople(String keyword, int offset, int limit) {
//        List<SearchPeopleDTO> list = repository.findPeople(keyword, offset, limit);
//
//        for(SearchPeopleDTO dto : list){
//            String url = dto.getPeopleProfileUrl();
//            dto.setPeopleProfileUrl("https://image.tmdb.org/t/p/original/" + url);
//        }
//        return list;

        return repository.findPeople(keyword, offset, limit);
    }

    @Override
    public int getPeopleCount(String keyword) {
        return repository.getPeopleCount(keyword);
    }

    @Override
    public List<SearchPostDTO> searchPosts(String keyword, int offset, int limit) {
        return repository.findPosts(keyword, offset, limit);
    }

    @Override
    public int getPostCount(String keyword) {
        return repository.getPostCount(keyword);
    }
}

