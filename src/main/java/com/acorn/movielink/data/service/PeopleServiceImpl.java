package com.acorn.movielink.data.service;

import com.acorn.movielink.data.repository.PeopleRepository;
import com.acorn.movielink.data.dto.PeopleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PeopleServiceImpl implements PeopleService{

    @Autowired
    PeopleRepository repository;

    @Autowired
    PeopleAPIExplorer peopleAPIExplorer;

    @Override
    public int insertPeople(PeopleDTO peopleDTO) {
        return repository.insertPeople(peopleDTO);
    }

    @Override
    public void savePeople(String movie_nm , String  releaseDate, String movie_id) throws IOException, InterruptedException {

        List<PeopleDTO> peopleDTOList = peopleAPIExplorer.getPeopleDTOList(movie_nm, releaseDate,movie_id);

        for(PeopleDTO peopleDTO :peopleDTOList) {
            repository.insertPeople(peopleDTO);
        }

    }



}
