package com.acorn.movielink.data.controller;


import com.acorn.movielink.data.PeopleAPIExplorer;
import com.acorn.movielink.data.dto.PeopleDTO;
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

@Slf4j
@Controller
public class PeopleController {

    @Autowired
    PeopleAPIExplorer peopleAPIExplorer;

    @Autowired
    PeopleServiceImpl peopleService;


    @ResponseBody
    @GetMapping("/people")
    public ResponseEntity<Object> peopleTbl(){

        //영화 아이디,제목,개봉일 가져오기

        //영화인 찾기
        int successCount = 0;
        int failureCount = 0;
        try {
            ArrayList<PeopleDTO> peopleDTOList = peopleAPIExplorer.getPeopleDTOList("영화제목", "개봉일", "영화아이디");
            for (PeopleDTO peopleDTO : peopleDTOList) {
                try {
                    peopleService.insertPeople(peopleDTO);
                    successCount++;
                } catch (Exception e) {
                    failureCount++;
                    log.error("Failed to insert PeopleDTO: " + peopleDTO.getPeople_nm(), e);
                }
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error fetching PeopleDTO list", e);
            return new ResponseEntity<>("Failed to fetch PeopleDTO list", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String resultMessage = String.format("Success: %d, Failed: %d", successCount, failureCount);
        return new ResponseEntity<>(resultMessage, HttpStatus.OK);
    }

}
