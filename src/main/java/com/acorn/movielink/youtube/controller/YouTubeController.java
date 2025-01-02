package com.acorn.movielink.youtube.controller;

import com.acorn.movielink.youtube.service.YouTubeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.util.List;

@Controller
public class YouTubeController {
    private final YouTubeService youTubeService;

    public YouTubeController(YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
    }

    @GetMapping("/shorts")
    public String getMovieShorts(Model model) {
//        List<String> shorts = youTubeService.getMovieShorts("영화");
//        System.out.println(shorts);
//        model.addAttribute("shorts", shorts);
        return "shorts"; // 반환할 HTML 템플릿 이름
    }
}
