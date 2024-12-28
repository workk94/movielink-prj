package com.acorn.movielink.ranking.controller;

import org.springframework.ui.Model;
import com.acorn.movielink.ranking.dto.RankingDTO;
import com.acorn.movielink.ranking.service.RankingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/rank")
    public String getRanking(Model model) {
        List<RankingDTO> list = rankingService.selectMovieByRank();
        model.addAttribute("ranklist",list);
        System.out.println(list);
        System.out.println(list.get(0).getScoreAvg());
        return "rank";
    }
}
