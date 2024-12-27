package com.acorn.movielink.tier.service;

import com.acorn.movielink.tier.dto.TierGenreDTO;
import com.acorn.movielink.tier.repository.TierGenreMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TierGenreService {

    private final TierGenreMapper genreMapper;

    public TierGenreService(TierGenreMapper genreMapper) {
        this.genreMapper = genreMapper;
    }

    public List<TierGenreDTO> findAllGenres() {
        return genreMapper.findAllGenres();
    }

    public TierGenreDTO findByGenreId(int genreId) {
        return genreMapper.findByGenreId(genreId);
    }

    public List<TierGenreDTO> findGenresByMovieId(int movieId) {
        return genreMapper.findGenresByMovieId(movieId);
    }

    public int insertGenre(TierGenreDTO genre) {
        return genreMapper.insertGenre(genre);
    }

    public int updateGenre(TierGenreDTO genre) {
        return genreMapper.updateGenre(genre);
    }

    public int deleteGenre(int genreId) {
        return genreMapper.deleteGenre(genreId);
    }
}
