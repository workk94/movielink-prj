package com.acorn.movielink.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class ScheduledTasks {

    private final MovieService movieService;
    private final RawDataService rawDataService;

    @Autowired
    public ScheduledTasks(MovieService movieService, RawDataService rawDataService) {
        this.movieService = movieService;
        this.rawDataService = rawDataService;
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 00시에 실행
    public void execute(){

        // 서버의 로컬 타임존에 따라 동작
        //String startDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 실행 순간의 Asia-Seoul의 시간
        String startDate = LocalDate.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String endDate = startDate;

        rawDataService.saveDataByPeriod(startDate, endDate);
        movieService.saveMovie();
    }
}
