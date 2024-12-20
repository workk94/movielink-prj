package com.acorn.movielink.youtube;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;

import java.util.List;

@Controller
public class YouTubeController {
    private final YouTubeService youTubeService;

    public YouTubeController(YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
    }

    @GetMapping("/shorts")
    public String getMovieShorts(@RequestParam(name = "query") String query, Model model) {
        List<String> shorts = youTubeService.getMovieShorts(query);
        System.out.println(shorts);
        model.addAttribute("shorts", shorts);
        return "shorts"; // 반환할 HTML 템플릿 이름
    }
}
