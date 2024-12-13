package com.acorn.movielink.data.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PeopleDTO {

    String movie_id;
    String people_id;
    String people_nm;
    String people_nm_en;
    String people_role_nm;      // 역할명이 없는경우 있음
    String people_profile_url;
    String people_type;



}
