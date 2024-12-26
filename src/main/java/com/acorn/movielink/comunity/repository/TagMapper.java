package com.acorn.movielink.comunity.repository;

import com.acorn.movielink.comunity.dto.TagDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper {

    // 인기 태그 조회
    List<TagDTO> selectTopTags();
    // 게시글 ID로 태그 조회
    List<TagDTO> selectTagsByPostId(@Param("postId") int postId);
    // 태그 존재 여부 확인
    Integer checkTagExists(@Param("tagName") String tagName);

    // 새로운 태그 삽입
    int insertTag(TagDTO tagDTO);

//    // 게시글-태그 관계 중복 여부 확인
//    int checkPostTagExists(@Param("postId") int postId, @Param("tagId") int tagId);

    // 게시글-태그 관계 삭제
    void deletePostTags(@Param("postId") int postId);

    // 태그 삽입 또는 조회
    int insertOrGetTagId(@Param("tagName") String tagName);

    // 게시글-태그 관계 추가
    void insertPostTag(@Param("postId") int postId, @Param("tagId") int tagId);
    void processTagsForPost(int postId, List<TagDTO> tagNames);


}
