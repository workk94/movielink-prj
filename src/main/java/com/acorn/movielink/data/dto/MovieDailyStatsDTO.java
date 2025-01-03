package com.acorn.movielink.data.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MovieDailyStatsDTO {

    String movie_nm;
    double sales_share;
    int audi_cnt;
    String open_dt;

}
