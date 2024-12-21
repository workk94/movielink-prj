package com.acorn.movielink.people_detail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    private Integer postId;
    private Integer memId;
    private String postTitle;
    private String content;
    private LocalDateTime postCreatedAt;
    private Integer postViews;
    private Integer postLikeCnt;
    private Integer postReport;

    private String postImgConvertedFileNm;

    private List<Tag> tags;
}
