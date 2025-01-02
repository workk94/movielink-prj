package com.acorn.movielink.movie_detail.controller;

import com.acorn.movielink.movie_detail.dto.MovieDTO;
import com.acorn.movielink.movie_detail.service.MovieDetailService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MovieDetailController2 {

    private final MovieDetailService2 service;


    @Autowired
    public MovieDetailController2(MovieDetailService2 service) {
        this.service = service;
    }


//    @GetMapping("/title/{movieId}")
//    public String getMovieDetail(@PathVariable("movieId") int movieId, Model model) {
//        MovieDTO dto = service.getMovieDetail(movieId);
//
//        if (dto != null){
//            model.addAttribute("movie", dto);
//        }
//
//        return "movie-detail";
//    }
}
