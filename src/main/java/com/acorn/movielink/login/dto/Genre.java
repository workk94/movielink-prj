package com.acorn.movielink.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    private Integer genreId;
    private String genreNm;
    private Integer movieId;

}
