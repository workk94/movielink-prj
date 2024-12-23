package com.acorn.movielink.people_detail.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PeopleLikeMapper {

    void insertLike(@Param("peopleId") Integer peopleId, @Param("memId") Integer memId);

    void deleteLike(@Param("peopleId") Integer peopleId, @Param("memId") Integer memId);

    int countLikes(@Param("peopleId") Integer peopleId);

    boolean isLiked(@Param("peopleId") Integer peopleId, @Param("memId") Integer memId);
}
