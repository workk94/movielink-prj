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
public class MovieLike {
    private int movieLikeId;  // 좋아요 ID
    private int movieId;      // 영화 ID
    private int memId;        // 사용자 ID
    private LocalDateTime movieLikeAt; // 좋아요 시간
}
