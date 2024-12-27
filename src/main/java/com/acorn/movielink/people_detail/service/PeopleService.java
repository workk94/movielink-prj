package com.acorn.movielink.people_detail.service;

import com.acorn.movielink.MovieMapper;
import com.acorn.movielink.people_detail.dto.People;
import com.acorn.movielink.people_detail.repository.PeopleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PeopleService {
    @Autowired
    private PeopleMapper peopleMapper;
    private MovieMapper movieMapper;

    public People getPeopleById(Integer peopleId) {
        return peopleMapper.selectPeopleById(peopleId);
    }


}
