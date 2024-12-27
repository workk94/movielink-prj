package com.acorn.movielink.data.service;

import com.acorn.movielink.data.dto.BoxOfficeDataDTO;

import java.util.List;
import java.util.Map;

public interface BoxOfficeDataService {

    // 시작일부터 종료일까지 박스오피스 데이터 등록
    int saveDataByPeriod(String startDate, String endDate);

    // 시작일부터 종료일까지 박스오피스 데이터 조회
    List<BoxOfficeDataDTO> findDataBetween(String startDate, String endDate);

    // 시작일부터 종료일까지 박스오피스 데이터 삭제
    int deleteDataBetween(String startDate, String endDate);

    // 영화 코드 불러오기
    List<Map<String, String>> getAllMovieCode();
}
