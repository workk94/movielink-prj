package com.acorn.movielink.search.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SearchMovieDTO {
    private Integer movieId;
    private String movieNm;
    private String movieGenre;
    private Double movieScoreAvg;
    private String moviePoster;
}
