package com.acorn.movielink.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {
    private Integer reviewId;
    private Integer memId;
    private Integer movieId;
    private String reviewTitle;
    private String reviewContent;
    private Integer reviewRating;
    private LocalDateTime reviewRegAt;
    private Integer reviewLikeCnt;

    private String movieNm;
    private String memNn; // 추가된 필드
}
