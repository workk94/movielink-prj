// Post.java
package com.acorn.movielink.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    private Integer postId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Integer post_view;
    private Integer post_like_cnt;


}
