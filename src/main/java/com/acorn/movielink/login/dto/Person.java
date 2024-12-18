package com.acorn.movielink.login.dto;

import lombok.Data;

@Data
public class Person {
    private Integer peopleId;
    private String peopleNm;
    private String peopleNmEn;
    private String peopleRoleNm;
    private String peopleProfileUrl;
    private String peopleType;
    private Integer movieId;
}
