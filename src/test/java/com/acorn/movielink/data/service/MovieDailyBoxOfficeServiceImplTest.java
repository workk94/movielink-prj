package com.acorn.movielink.data.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MovieDailyBoxOfficeServiceImplTest {

    @Autowired
    MovieDailyBoxOfficeService service;

    @Test
    void test1(){
        service.getDailyStats("20241218");
    }

}