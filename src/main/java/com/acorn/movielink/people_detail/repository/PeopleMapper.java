package com.acorn.movielink.people_detail.repository;

import com.acorn.movielink.people_detail.dto.People;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PeopleMapper {
    People selectPeopleById(@Param("peopleId") Integer peopleId);

}

