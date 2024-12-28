package com.acorn.movielink.search.dto;

import lombok.Data;

@Data
public class SearchMovieDTO {
    private int movieId;
    private String movieNm;
    private String movieRlsYear;
    private String movieGenre;
    private Double movieScoreAvg;
    private String moviePoster;
}
