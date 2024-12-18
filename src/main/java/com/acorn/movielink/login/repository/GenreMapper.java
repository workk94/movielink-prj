package com.acorn.movielink.login.repository;

import com.acorn.movielink.login.dto.Genre;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface GenreMapper {

    // 모든 장르 조회
    List<Genre> findAllGenres();

    // 장르 ID로 특정 장르 조회
    Optional<Genre> findByGenreId(@Param("genreId") Integer genreId);

    // 새로운 장르 추가
    void insertGenre(Genre genre);

    // 기존 장르 수정
    void updateGenre(Genre genre);

    // 장르 삭제
    void deleteGenre(@Param("genreId") Integer genreId);

    // 특정 영화에 속한 장르 조회
    List<Genre> findGenresByMovieId(@Param("movieId") Integer movieId);
}
