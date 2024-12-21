package com.acorn.movielink.people_detail.repository;

import com.acorn.movielink.people_detail.dto.People;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PeopleMapper {
    People selectPeopleById(@Param("peopleId") Integer peopleId);
}
