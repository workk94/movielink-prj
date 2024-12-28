package com.acorn.movielink.data.controller;

import com.acorn.movielink.data.dto.BoxOfficeMovieDTO;
import com.acorn.movielink.data.dto.MovieDailyStatsDTO;
import com.acorn.movielink.data.service.MovieDailyBoxOfficeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

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
        String yesterdayDate = "";
        if (date == null || date.isEmpty()) {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            yesterdayDate = yesterday.toString();
        }else {
            yesterdayDate = date;
        }
        List<MovieDailyStatsDTO> movieDailyStatsList = boxOfficeService.getDailyStats(yesterdayDate);


        Map<String, Object> response = new HashMap<>();

        if(!movieDailyStatsList.isEmpty()) {
            int movieRank = 0;
            List<BoxOfficeMovieDTO> boxOfficeMovieList = new ArrayList<>();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (MovieDailyStatsDTO movie : movieDailyStatsList) {
                if (movieRank >= 10) {
                    break;
                }
                String formattedDate = "";
                try {
                    Date openDate = originalFormat.parse(movie.getOpen_dt());
                    formattedDate = dateFormat.format(openDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    boxOfficeMovieList.clear();
                    break;
                }
                BoxOfficeMovieDTO boxOfficeMovie = boxOfficeService.getBoxOfficeMovies(movie.getMovie_nm(), formattedDate);
                boxOfficeMovieList.add(boxOfficeMovie);
                movieRank++;
            }
            response.put("boxOfficeMovieList", boxOfficeMovieList);
            System.out.println(boxOfficeMovieList.get(0));
        }
        response.put("moviesList", movieDailyStatsList);
        return response;
    }


}
