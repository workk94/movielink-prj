package com.acorn.movielink.data.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PeopleServiceImplTest {
    @Autowired
    PeopleService service;

    @Test
    void test() throws IOException, InterruptedException {
        service.savePeople("소방관", "2024", "1");
    }
}