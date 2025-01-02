package com.acorn.movielink.comunity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostDTO {

    private int postId;
    private int memId;
    private String postTitle;
    private String content;
    private Date postCreatedAt;
    private Date postUpdatedAt;
    private int postViews;
    private int postLikeCnt;
    private int postReport;
    private String memNn;
    private String thumbnailUrl; // 이미지 url

    private List<TagDTO> tags; // 유지 (DTO 기반 처리로 일관성 유지)



}
