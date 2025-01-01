package com.acorn.movielink.movie_detail.dto;

import lombok.Data;

import java.util.List;

@Data
public class MovieDTO {
    private int movieId;
    private String movieNm;
    private String movieNmEn;
    private String movieOpenDt;
    private String movieAgeRating;
    private int movieRunningTime;
    private String movieCountry;
    private String moviePlot;
    private String movieTrailer;
    private String moviePoster;
    private int genreId;
    private String genreNm;
    private double movieScoreAvg;
    private double movieImdbScore;
    private double movieTmdbScore;

    private List<PeopleDTO> directors;
    private List<PeopleDTO> actors;

}
