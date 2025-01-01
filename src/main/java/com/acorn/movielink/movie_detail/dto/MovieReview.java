package com.acorn.movielink.movie_detail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieReview {
    private int reviewId;            // 리뷰 ID
    private int movieId;             // 영화 ID
    private int memId;               // 사용자 ID
    private int reviewLikeCnt;
    private String reviewContent;    // 리뷰 내용
    private double reviewRating;     // 리뷰 점수
    private LocalDateTime reviewRegAt;  // 리뷰 작성 시간
    private LocalDateTime reviewUpdatedAt;  // 리뷰 수정 시간

    private String memNn;
    private String memProfileFilePath;
}
