package com.acorn.movielink.tier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TierMovieDTO {

    private Integer movieId;
    private String movieNm;
    private String moviePosterUrl;
    private String movieOpenDt;

    private String genre;
}
