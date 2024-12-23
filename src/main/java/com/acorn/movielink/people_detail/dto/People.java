package com.acorn.movielink.people_detail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class People {
    private Integer peopleId;
    private String peopleNm;
    private String peopleNmEn;
    private String peopleRoleNm;
    private String peopleProfileUrl;
    private String peopleType;
    private Integer movieId;
}
