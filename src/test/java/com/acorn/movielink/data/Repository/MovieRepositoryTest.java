package com.acorn.movielink.data.repository;

import com.acorn.movielink.data.dto.MovieDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class MovieRepositoryTest {
    @Autowired
    MovieRepository repository;

    @Test
    void test() {
        MovieDTO dto = new MovieDTO();
        repository.insertMovie(dto);
        System.out.println(dto.getMovieId());
    }
}