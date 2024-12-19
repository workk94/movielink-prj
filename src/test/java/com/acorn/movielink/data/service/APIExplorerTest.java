package com.acorn.movielink.data.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class APIExplorerTest {
    @Autowired
    APIExplorer explorer;

    @Test
    void test() {
        String result = explorer.getDailyBoxOfficeData("20241215");
        System.out.println(result);
    }

    @Test
    void test2() {
        String result = explorer.getIMDBData("Moana 2", "2024");
        System.out.println(result);
    }

    @Test
    void test3() {
        String result = explorer.getMovieDataFromKOBIS("20200142");
        System.out.println(result);
    }

    @Test
    void test4() {
        String result = explorer.getMovieDataFromKMDB("군함도", "20170726");
        System.out.println(result);
    }

    @Test
    void test5(){
        String result = explorer.getMovieDataFromTMDB("모아나 2", "2024");
        System.out.println(result);
    }
}