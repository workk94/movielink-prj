package com.acorn.movielink.data.service;

import com.acorn.movielink.data.dto.MovieDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MovieServiceImplTest {

    @Autowired
    private MovieService service;

    @Test
    void test() throws Exception {
        int result = service.saveMovie();
        assertTrue(result > 0);
    }

    @Test
    void test2(){
        List<MovieDTO> list = service.getMovieList();
        list.forEach(System.out::println);
    }
}