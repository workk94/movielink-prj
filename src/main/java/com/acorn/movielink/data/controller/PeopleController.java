package com.acorn.movielink.data.controller;


import com.acorn.movielink.data.PeopleAPIExplorer;
import com.acorn.movielink.data.dto.MovieInfoDTO;
import com.acorn.movielink.data.dto.PeopleDTO;
import com.acorn.movielink.data.service.MovieServiceImpl;
import com.acorn.movielink.data.service.PeopleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class PeopleController {

    @Autowired
    PeopleAPIExplorer peopleAPIExplorer;

    @Autowired
    PeopleServiceImpl peopleService;

    @Autowired
    MovieServiceImpl movieService;

//    @ResponseBody
//    @GetMapping("/people")
//    public ResponseEntity<Object> peopleTbl(){
//
//        //영화 아이디,제목,개봉일 가져오기
//
//        List<MovieInfoDTO> movieArrayList = movieService.getAllMovie();
//
//        //List<MovieInfoDTO> movieArrayList = new ArrayList<>();
///*
//        movieArrayList.add(new MovieInfoDTO("1234","Look Back","2024"));
//        movieArrayList.add(new MovieInfoDTO("2345","The Boy and the Heron","2023"));
//        movieArrayList.add(new MovieInfoDTO("3456","One Win (1seung)","2024"));
//        MovieInfoDTO movie22 = new MovieInfoDTO("4567","Firefighters","20241204");
//        movieArrayList.add(movie22);
//*/
//
//
//        //영화인 찾기
//        int successCount = 0;
//        int failureCount = 0;
//        for(MovieInfoDTO movie : movieArrayList) {
//            try {
//                ArrayList<PeopleDTO> peopleDTOList = peopleAPIExplorer.getPeopleDTOList(movie.getMovie_nm_en(), movie.getMovie_open_dt(), movie.getMovie_id());
//                for (PeopleDTO peopleDTO : peopleDTOList) {
//                    try {
//                        peopleService.insertPeople(peopleDTO);
//                        successCount++;
//                    } catch (Exception e) {
//                        failureCount++;
//                        failureCount++; log.error("Failed to insert PeopleDTO: {}", peopleDTO.getPeople_nm(), e);
//                    }
//                }
//                //Thread.sleep(8000);
//            } catch (IOException | InterruptedException e) {
//                log.error("Error fetching PeopleDTO list for movie: {}", movie.getMovie_nm_en(), e);
//                return new ResponseEntity<>("Failed to fetch PeopleDTO list", HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }
//        String resultMessage = String.format("Success: %d, Failed: %d", successCount, failureCount);
//        return new ResponseEntity<>(resultMessage, HttpStatus.OK);
//    }





}
