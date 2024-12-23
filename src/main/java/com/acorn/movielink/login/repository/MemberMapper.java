package com.acorn.movielink.login.repository;

import com.acorn.movielink.login.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {
    void insertMember(Member member);

    Optional<Member> findByMemEmail(@Param("memEmail") String memEmail);

    Optional<Member> findByMemSnsId(@Param("memSnsId") String memSnsId);

    void updatePassword(@Param("memId") Integer memId, @Param("memPw") String memPw);

    void updateMember(Member updatedMember);

    Optional<Member> findByMemId(@Param("memId") Integer memId);

    void deleteMember(@Param("memId") Integer memId);

    void insertMemberGenre(@Param("genreId") Integer genreId, @Param("memId") Integer memId);

    // 추가: 특정 회원의 장르 조회
    List<Integer> findGenreIdsByMemId(@Param("memId") Integer memId);

    // 좋아요한 인물 조회
    List<Person> findLikedPersonsByMemId(@Param("memId") Integer memId);

    // 작성한 게시글 조회
    List<Post> findWrittenPostsByMemId(Integer memId);

    // 좋아요한 영화 조회 추가
    List<Movie> findLikedMoviesByMemId(@Param("memId") Integer memId);

    //구매 아이템 조회
    List<Item> findPurchasedItemByMemId(@Param("memId") Integer memId);

    void deleteMemberGenres(@Param("memId") Integer memId);

    // 최신 10개 리뷰 조회
    List<Review> findLatestReviews();

    // 사용자 장르에 맞는 10개 영화 추천
    List<Movie> findRecommendedMovies(Integer memId);
}
