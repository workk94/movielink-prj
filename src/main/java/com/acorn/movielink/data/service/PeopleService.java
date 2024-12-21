package com.acorn.movielink.data.service;

import com.acorn.movielink.data.dto.PeopleDTO;

import java.io.IOException;

public interface PeopleService {

    int insertPeople(PeopleDTO peopleDTO);

    void savePeople(String movie_nm , String  releaseDate, String movie_id) throws IOException, InterruptedException;
}
