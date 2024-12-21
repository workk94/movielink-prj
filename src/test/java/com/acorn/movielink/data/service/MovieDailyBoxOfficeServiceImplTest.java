package com.acorn.movielink.data.service;

import com.acorn.movielink.data.dto.MovieDailyStatsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MovieDailyBoxOfficeServiceImplTest {

    @Autowired
    MovieDailyBoxOfficeService service;

    @Test
    void test1(){
        List<MovieDailyStatsDTO> list =  service.getDailyStats("20241218");

        for(MovieDailyStatsDTO item : list){System.out.println(item);};

    }

}