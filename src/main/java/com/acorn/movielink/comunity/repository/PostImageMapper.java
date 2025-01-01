package com.acorn.movielink.comunity.repository;

import com.acorn.movielink.comunity.dto.PostImageDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostImageMapper {


    // 게시글 이미지 저장
    int insertPostImage(PostImageDTO dto);

    // 게시글 이미지 조회(썸네일)
    PostImageDTO selectPostImage(int postId);

    // 게시글 이미지 조회
    List<PostImageDTO> selectAllPostImages(int postId);
}
