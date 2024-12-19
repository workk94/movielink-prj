package com.acorn.movielink.data.Repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RawDataRepositoryTest {
    @Autowired
    private RawDataRepository repository;

    @Test
    void test1(){
        List<Map<String, String>> list =  repository.selectMovieCodes();
        list.forEach(System.out::println);
    }

}