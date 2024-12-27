package com.acorn.movielink.comunity.repository;

import com.acorn.movielink.comunity.dto.PostDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityPostMapper {


    List<PostDTO> selectAllList();
    List<PostDTO> selectTopTenPosts();
    PostDTO selectPostById(@Param("postId") int postId);
    void updatePostViews(@Param("postId") int postId);
    void updatePost(PostDTO postDTO);
    void deletePost(@Param("postId") int postId);

    List<PostDTO> selectTop7Member();
    List<PostDTO> selectOneMemberPostList(@Param("memId") int memId);

    void insertPost(PostDTO postDTO); // 태그 리스트 제거
}
