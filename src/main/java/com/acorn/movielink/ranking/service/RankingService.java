package com.acorn.movielink.ranking.service;

import com.acorn.movielink.ranking.dto.RankingDTO;
import com.acorn.movielink.ranking.repository.RankingMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingService {

    private final RankingMapper rankingmapper;

    public RankingService(RankingMapper rankingmapper) {
        this.rankingmapper = rankingmapper;
    }

    public List<RankingDTO> selectMovieByRank() {
        return rankingmapper.selectMovieByRank();
    }
}
