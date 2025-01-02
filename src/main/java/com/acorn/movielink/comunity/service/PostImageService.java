package com.acorn.movielink.comunity.service;

import com.acorn.movielink.comunity.dto.PostImageDTO;
import com.acorn.movielink.comunity.dto.UploadFile;
import com.acorn.movielink.comunity.repository.PostImageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostImageService {

    private PostImageMapper postImageMapper;

    @Autowired
    PostImageService (PostImageMapper postImageMapper){
        this.postImageMapper = postImageMapper;
    }

    // 게시글 이미지 저장
    public void savePostImages(int postId, List<UploadFile> uploadFiles) {
        for (UploadFile uploadFile : uploadFiles) {
            PostImageDTO postImage = new PostImageDTO();
            postImage.setPostId(postId);
            postImage.setOriginalFileNm(uploadFile.getOriginalFilename());
            postImage.setStoredFileNm(uploadFile.getStoreFileName());
            postImage.setFilePath(uploadFile.getFilePath());
            postImageMapper.insertPostImage(postImage);
        }
    }

    // 게시글 썸네일
    public PostImageDTO getThumbnail(int postId) {
        return postImageMapper.selectPostImage(postId);
    }

    // 게시글 썸네일 url
    public String getThumbnailUrl(int postId) {
        PostImageDTO thumbnail = postImageMapper.selectPostImage(postId);
        if (thumbnail != null) {
            return thumbnail.getFilePath()
                    .replace("/app/upload/post-images", "/upload/post-images")
                    + thumbnail.getStoredFileNm();
        }
        return null;
    }

    // 게시글 이미지 리스트 조회
    public List<PostImageDTO> getPostImagesByPostId(int postId) {
        return postImageMapper.selectAllPostImages(postId);
    }
}
