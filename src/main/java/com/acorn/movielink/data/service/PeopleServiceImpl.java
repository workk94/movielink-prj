package com.acorn.movielink.data.service;

import com.acorn.movielink.data.repository.PeopleRepository;
import com.acorn.movielink.data.dto.PeopleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PeopleServiceImpl implements PeopleService{

    @Autowired
    PeopleRepository repository;

    @Override
    public int insertPeople(PeopleDTO peopleDTO) {
        return repository.insertPeople(peopleDTO);
    }
}
