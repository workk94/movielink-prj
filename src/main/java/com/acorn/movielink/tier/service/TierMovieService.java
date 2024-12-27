package com.acorn.movielink.tier.service;

import com.acorn.movielink.tier.dto.TierMovieDTO;
import com.acorn.movielink.tier.repository.TierMovieMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TierMovieService {

    private final TierMovieMapper movieMapper;

    public TierMovieService(TierMovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    // (NEW) startYear, endYear, genreId에 따른 동적 필터
    public List<TierMovieDTO> findMoviesByFilter(Integer startYear, Integer genreId) {
        return movieMapper.findMoviesByFilter(startYear, genreId);
    }
}
