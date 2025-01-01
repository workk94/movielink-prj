package com.acorn.movielink.comunity.dto;

import lombok.Data;

@Data
public class PostImageDTO {
    private int postImgId; // 게시글 이미지 id
    private int postId; // 게시글 id
    private String originalFileNm; // 원본 파일명
    private String storedFileNm; // 서버에 저장된 파일명 (UUID)
    private String filePath; // 파일 저장 경로

    // 파일이 저장된 전체 경로를 반환
    public String getFullPath() {
        return filePath + storedFileNm;
    }
}
