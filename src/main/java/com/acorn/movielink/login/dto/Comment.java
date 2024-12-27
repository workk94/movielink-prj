package com.acorn.movielink.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Integer commentId;
    private Integer postId;
    private Integer memId;
    private Integer parentId;
    private String content;
    private LocalDateTime commentRegAt;
    private LocalDateTime commentUpdatedAt;
    private LocalDateTime commentDeletedAt;
    private Integer commentLikeCnt;
    
}
