package com.acorn.movielink.tier.controller;

import com.acorn.movielink.tier.dto.TierGenreDTO;
import com.acorn.movielink.tier.dto.TierMovieDTO;
import com.acorn.movielink.tier.service.TierGenreService;
import com.acorn.movielink.tier.service.TierMovieService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/tier")
public class TierController {

    private final TierMovieService movieService;
    private final TierGenreService genreService;

    public TierController(TierMovieService movieService, TierGenreService genreService) {
        this.movieService = movieService;
        this.genreService = genreService;
    }

    @GetMapping
    public String tierPage(Model model) {
        List<TierGenreDTO> genres = genreService.findAllGenres();
        model.addAttribute("genres", genres);
        return "tier"; // Thymeleaf 템플릿
    }


    @GetMapping("/movies")
    @ResponseBody
    public List<TierMovieDTO> getFilteredMovies(
            @RequestParam(value = "startYear", required = false) Integer startYear,

            @RequestParam(value = "genreId", required = false) Integer genreId
    ) {
        // Service(Mapper)에서 <if>문으로 조건을 적용
        return movieService.findMoviesByFilter(startYear, genreId);
    }
}
