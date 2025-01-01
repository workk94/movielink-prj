package com.acorn.movielink.comunity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadFile {
    private String originalFilename; // 원본 파일명
    private String storeFileName; // 서버에 저장된 파일명(UUID)
    private String filePath; // 파일 저장 경로
}
