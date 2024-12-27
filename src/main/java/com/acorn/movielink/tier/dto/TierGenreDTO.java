package com.acorn.movielink.tier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TierGenreDTO {
    private Integer genreId;  // pk
    private String genreNm;   // 장르명

}
