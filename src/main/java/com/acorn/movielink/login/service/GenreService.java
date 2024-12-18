package com.acorn.movielink.login.service;

import com.acorn.movielink.login.dto.Genre;
import com.acorn.movielink.login.repository.GenreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenreService {

    private final GenreMapper genreMapper;

    @Autowired
    public GenreService(GenreMapper genreMapper) {
        this.genreMapper = genreMapper;
    }

    // 모든 장르 조회
    public List<Genre> getAllGenres() {
        return genreMapper.findAllGenres();
    }

    // 장르 ID로 특정 장르 조회
    public Optional<Genre> getGenreById(Integer genreId) {
        return genreMapper.findByGenreId(genreId);
    }

    // 새로운 장르 추가
    public void addGenre(Genre genre) {
        genreMapper.insertGenre(genre);
    }

    // 기존 장르 수정
    public void updateGenre(Genre genre) {
        genreMapper.updateGenre(genre);
    }

    // 장르 삭제
    public void deleteGenre(Integer genreId) {
        genreMapper.deleteGenre(genreId);
    }
}
