package com.acorn.movielink.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movie { // 클래스 이름을 대문자로 수정
    private Integer movieId;
    private String movieNm;
    private String movieNmEn;
    private LocalDate movieOpenDt; // LocalDate로 수정
    private String movieAgeRating;
    private Integer movieRunningTime;
    private String movieCountry;
    private String moviePlot;
    private String movieTrailerUrl;
    private String moviePoster;
    private Double movieImdbScore; // DECIMAL(2,1) -> Double로 수정
    private Integer movieRottenScore;
    private LocalDateTime movieRegAt;
}
