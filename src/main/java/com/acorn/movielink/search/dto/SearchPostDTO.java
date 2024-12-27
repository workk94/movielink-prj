package com.acorn.movielink.search.dto;

import lombok.Data;

@Data
public class SearchPostDTO {
    private int postId; // 게시글 아이디
    private String postTitle; // 게시글 제목
    private int postViews; // 게시글 뷰
    private int postLikeCnt; // 추천 수
    private int commentCnt; // 댓글 수
//    private String postImgId; // 대표이미지
}
