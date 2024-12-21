package com.acorn.movielink.data.service;

import com.acorn.movielink.data.dto.KMDBMovieActor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class PeopleAPIExplorerTest {
    @Autowired
    PeopleAPIExplorer explorer;

    @Test
    void test() throws IOException {
        String json = explorer.getKMDBMovieDatas("무파사: 라이온 킹", "20241218");
        List<KMDBMovieActor> list = explorer.getKMDBMovieActorList(json, "무파사: 라이온 킹");
    }
}