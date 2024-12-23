package com.acorn.movielink.people_detail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeopleLike {
    private Integer peopleLikeId;
    private LocalDateTime peopleLikeAt;
    private Integer peopleId;
    private Integer memId;
}
