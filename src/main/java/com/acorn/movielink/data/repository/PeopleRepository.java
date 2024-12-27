package com.acorn.movielink.data.repository;

import com.acorn.movielink.data.dto.PeopleDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PeopleRepository {

    @Autowired
    SqlSession session;

    public int insertPeople(PeopleDTO peopleDTO){
        //매퍼 충돌나서 변경함
        //return session.insert("com.acorn.movielink.data.PeopleMapper.insertPeople",peopleDTO);
        return session.insert("com.acorn.movielink.people_detail.repository.PeopleMapper.insertPeople",peopleDTO);
    }


}
