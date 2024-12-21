package com.acorn.movielink.people_detail.repository;

import com.acorn.movielink.people_detail.dto.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    List<Post> selectPostsByTagName(@Param("tagName") String tagName);
}
