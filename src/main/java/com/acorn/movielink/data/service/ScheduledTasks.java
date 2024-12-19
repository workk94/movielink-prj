package com.acorn.movielink.data.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class ScheduledTasks {

    private final MovieService movieService;
    private final RawDataService rawDataService;

    @Autowired
    public ScheduledTasks(MovieService movieService, RawDataService rawDataService) {
        this.movieService = movieService;
        this.rawDataService = rawDataService;
    }

    // 초 분 시 일 월
    //@Scheduled(cron = "0 */1 * * * *")
    @Scheduled(cron = "0 38 15 * * *")
    public void execute(){

        // 서버의 로컬 타임존에 따라 동작
        //String startDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 실행 순간의 Asia-Seoul의 시간

        try {

            String startDate = LocalDate.now(ZoneId.of("Asia/Seoul"))
                    .minusDays(1) // 하루 전 날짜
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String endDate = startDate;

            rawDataService.saveDataByPeriod(startDate, endDate);
            movieService.saveMovie();
        } catch (Exception e){
            log.info(e.getMessage());
        }
    }
}
