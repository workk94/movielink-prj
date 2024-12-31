package com.acorn.movielink.data.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BoxOfficeMovieDTO {

    Long movie_id;
    String movie_nm;
    String movie_poster;

}
