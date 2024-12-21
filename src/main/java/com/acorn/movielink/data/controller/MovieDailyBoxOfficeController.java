package com.acorn.movielink.data.controller;

import com.acorn.movielink.data.dto.MovieDailyStatsDTO;
import com.acorn.movielink.data.service.MovieDailyBoxOfficeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MovieDailyBoxOfficeController {

    @Autowired
    MovieDailyBoxOfficeServiceImpl boxOfficeService;

    @GetMapping("/boxoffice")
    public String boxoffice(){
        return "boxoffice";
    }

    @ResponseBody
    @GetMapping("/boxoffice/daily")
    public Map<String, Object> boxoffice(@RequestParam(value = "date", required = false) String date ) {
        if (date == null || date.isEmpty()) {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            date = yesterday.toString();
        }

        List<MovieDailyStatsDTO> movieDailyStatsList = boxOfficeService.getDailyStats(date);
        Map<String, Object> response = new HashMap<>();
        response.put("moviesList", movieDailyStatsList);

        return response;
    }


}
