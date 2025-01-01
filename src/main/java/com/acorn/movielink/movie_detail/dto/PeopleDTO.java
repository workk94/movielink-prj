package com.acorn.movielink.movie_detail.dto;

import lombok.Data;

@Data
public class PeopleDTO {
        private int peopleId;
        private String peopleNm;
        private String peopleRoleNm;
        private String peopleProfileUrl;
        private String peopleType;
}
