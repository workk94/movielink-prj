package com.acorn.movielink.data.dto;

import com.acorn.movielink.people_detail.dto.People;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {
    public double getCombineScore;
    private Integer movieId; // 0. db 영화 아이디
    private String movieNm;       // 1. 영화 이름
    private String movieNmEn;       // 2. 영화 이름 영문
    private String movieOpenDt;        // 3. 영화 개봉일
    private String movieAgeRating;        // 4. 영화 연령 등급
    private String movieRunningTime;      // 5. 영화 상영 시간
    private String movieNation; // 6. 제작국가
    private Integer genreId;     // 장르 아이디 추가
    private String genre;        // 7. 장르
    private String moviePlot;  // 8. 영화 줄거리
    private String movieTrailerUrl; // 9. 영화 예고편
    private String moviePosterUrl; // 10. 영화 대표 포스터
    private double movieImdbScore; // 11. 영화 IMDB점수
    private double movieTMDBScore; // 12. 영화 TMDB 점수
    private List<People> peopleList; // 출연진 정보

    public double getCombinedScore() {
        return ((this.movieImdbScore * 10) + this.movieTMDBScore) / 2;
    }

}