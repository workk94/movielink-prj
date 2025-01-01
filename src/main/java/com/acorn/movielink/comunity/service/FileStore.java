package com.acorn.movielink.comunity.service;


import com.acorn.movielink.comunity.dto.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.post-images-dir}")
    private String filePath;

    private final List<String> allowedExt = List.of("jpg", "jpeg", "png", "gif"); // 허용 확장자

    // 전체 경로
    public String getFullPath(String filename) {
        return filePath + filename;
    }

    // 이미지 파일 저장
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String ext = extractExt(originalFilename);

        if (!isAllowedExtension(ext)) {
            throw new IllegalArgumentException("허용되지 않은 파일 형식입니다: " + ext);
        }

        String storeFilename = createStoredFileName(originalFilename);
        File file = new File(getFullPath(storeFilename));

        // 폴더가 존재하지 않으면 생성
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        multipartFile.transferTo(file);
        return new UploadFile(originalFilename, storeFilename, filePath);
    }

    // 여러 이미지 파일 저장
    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> result = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                result.add(storeFile(multipartFile));
            }
        }
        return result;
    }

    // 허용된 확장자인지 확인
    private boolean isAllowedExtension(String ext) {
        return allowedExt.contains(ext.toLowerCase());
    }

    // 파일명(UUID) 생성
    private String createStoredFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 확장자 추출
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}




