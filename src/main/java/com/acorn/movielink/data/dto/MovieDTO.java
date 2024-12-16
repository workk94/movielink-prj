package com.acorn.movielink.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class MovieDTO {

    String movie_id;
    String movie_nm_en;
    String movie_open_dt;

}
