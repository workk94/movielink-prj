package com.acorn.movielink.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoxOfficeDataDTO {
    String rnum; // 박스오피스 순번
    String ranking; // 해당일자 박스오피스 순위
    String rankInten; // 전일대비 순위의 증감분
    String rankOldAndNew; // 랭킹 신규 진입 여부
    String movieCd; // 영화의 대표코드
    String movieNm; // 영화 이름
    String openDt; // 영화 개봉일
    String salesAmt; // 해당일의 매출액
    String salesShare; // 해당일자 매출 총액 대비 해당 영화의 매출 비율
    String salesInten; // 전일 대비 매출액 증감분
    String salesChange; // 전일 대비 매출액 증감 비율
    String salesAcc; // 누적매출액
    String audiCnt; // 해당일의 관객수
    String audiInten; // 전일 대비 관객수 증감분
    String audiChange; // 전일 대비 관객수 증감 비율
    String audiAcc; // 누적관객수
    String scrnCnt; // 	해당일자에 상영한 스크린수
    String showCnt; // 해당일자에 상영된 횟수
    String boxOfficeDt; // 박스오피스 일자
}
