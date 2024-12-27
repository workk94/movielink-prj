package com.acorn.movielink.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice {
    private Integer notificationId;
    private String title;
    private String content;
    private LocalDateTime noticeDate;
    private Integer memId;


}
