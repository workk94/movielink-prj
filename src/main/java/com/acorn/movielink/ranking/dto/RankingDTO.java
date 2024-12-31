package com.acorn.movielink.ranking.dto;

import lombok.Data;

@Data
public class RankingDTO {
    public double getCombineScore;
    private Integer movieId; // 0. db 영화 아이디
    private String movieNm;       // 1. 영화 이름
    private String movieNmEn;       // 2. 영화 이름 영문
    private String moviePosterUrl; // 10. 영화 대표 포스터
    private double scoreAvg;
    private double movieImdbScore; // 11. 영화 IMDB점수
    private double movieTMDBScore; // 12. 영화 TMDB 점수


    public String getCombinedScore() {
        double combinedScore = (this.scoreAvg + this.movieImdbScore + this.movieTMDBScore) / 3;
        return String.format("%.1f", combinedScore);
    }

}
