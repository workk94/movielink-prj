package com.acorn.movielink.tier.repository;

import com.acorn.movielink.tier.dto.TierGenreDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TierGenreMapper {

    // 모든 장르 조회
    List<TierGenreDTO> findAllGenres();

    // 특정 장르 조회
    TierGenreDTO findByGenreId(@Param("genreId") Integer genreId);

    // 특정 영화(movieId)에 속한 장르 조회 (DB 설계에 따라 다름)
    List<TierGenreDTO> findGenresByMovieId(@Param("movieId") Integer movieId);

    // 장르 추가
    int insertGenre(TierGenreDTO genre);

    // 장르 수정
    int updateGenre(TierGenreDTO genre);

    // 장르 삭제
    int deleteGenre(@Param("genreId") Integer genreId);
}
