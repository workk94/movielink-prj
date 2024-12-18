package com.acorn.movielink.data.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class APIExplorer {

    private final String kobisAPIKey;
    private final String KMDBAPIKey;
    private final String RAPIDAPIKey;
    private final String youtubeAPIKey;
    private final String TMDBAPIKey;

    public APIExplorer(
            @Value("${kobis-api-key}") String kobisAPIKey,
            @Value("${kmdb-api-key}") String KMDBAPIKey,
            @Value("${rapid-api-key}") String RAPIDAPIKey,
            @Value("${youtube-api-key}") String youtubeAPIKey,
            @Value("${tmdb-api-key}") String TMDBAPIKey

    ) {
        this.kobisAPIKey = kobisAPIKey;
        this.KMDBAPIKey = KMDBAPIKey;
        this.RAPIDAPIKey = RAPIDAPIKey;
        this.youtubeAPIKey = youtubeAPIKey;
        this.TMDBAPIKey = TMDBAPIKey;
    }

    // URL로 데이터 불러오기
    private String fetchData(StringBuilder urlBuilder, Map<String, String> headers) {
        try {
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            // 헤더 부분 추가
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }
            }

            log.info("응답코드: {}", conn.getResponseCode());
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            return sb.toString();
        } catch (IOException e) {
            log.error("API 요청 실패: {}", urlBuilder, e);
            return null;
        }
    }

    // KOBIS 일일 박스오피스 데이터를 가져오는 메서드
    // targetDt : YYYYMMDD
    public String getDailyBoxOfficeData(String targetDt) {
        StringBuilder urlBuilder = new StringBuilder("http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json");
        urlBuilder.append("?key=").append(kobisAPIKey);
        urlBuilder.append("&targetDt=").append(encodeParam(targetDt));

        return fetchData(urlBuilder, null);
    }

    // KOBIS 영화코드로 영화 정보 조회
    public String getMovieDataFromKOBIS(String movieCd) {
        StringBuilder urlBuilder = new StringBuilder("http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json");
        urlBuilder.append("?key=").append(kobisAPIKey);
        urlBuilder.append("&movieCd=").append(encodeParam(movieCd));

        return fetchData(urlBuilder, null);
    }

    public String getMovieDataFromTMDB(String title, String releaseDts){
        StringBuilder urlBuilder = new StringBuilder("https://api.themoviedb.org/3/search/movie");
        urlBuilder.append("?key=").append(kobisAPIKey);
        urlBuilder.append("&query=").append(encodeParam(title));
        urlBuilder.append("&include_adult=true");
        urlBuilder.append("&language=ko-kr");
        //urlBuilder.append("&primary_release_year=").append(encodeParam(releaseDts.substring(0,4)));

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + TMDBAPIKey);
        headers.put("accept", "application/json");

        return fetchData(urlBuilder, headers);
    }

    public String getMovieDataFromYoutube(){
        return null;
    }

    // KMDB 영화 정보를 가져오는 메서드
    // releaseDts : YYYYMMDD
    public String getMovieDataFromKMDB(String title, String releaseDts) {
        StringBuilder urlBuilder = new StringBuilder("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp");
        urlBuilder.append("?ServiceKey=").append(KMDBAPIKey);
        urlBuilder.append("&collection=kmdb_new2");
        urlBuilder.append("&title=").append(encodeParam(title));
        urlBuilder.append("&releaseDts=").append(encodeParam(releaseDts));

        return fetchData(urlBuilder, null);
    }

    // 영화 영문명과 개봉년도로 IMDB 코드값 조회
    // 개봉년도 YYYY
    public String getIMDBData(String movieNmEn, String rlsYear) {
        StringBuilder urlBuilder = new StringBuilder("https://imdb236.p.rapidapi.com/imdb/search");
        urlBuilder.append("?type=movie");
        urlBuilder.append("&sortField=startYear");
        urlBuilder.append("&sortOrder=DESC");
        //urlBuilder.append("&originalTitle=").append(encodeParam(movieNmEn)); // primaryTitle로 검색하는게 검색 결과 잘 나옴
        urlBuilder.append("&primaryTitle=").append(encodeParam(movieNmEn));
        urlBuilder.append("&startYearTo=").append(encodeParam(rlsYear));
        urlBuilder.append("&startYearFrom=").append(encodeParam(rlsYear));

        Map<String, String> headers = new HashMap<>();
        headers.put("x-rapidapi-host", "imdb236.p.rapidapi.com");
        headers.put("x-rapidapi-key", RAPIDAPIKey);

        return fetchData(urlBuilder, headers);
    }

    public String getYoutubeData(String movieNm) {
        StringBuilder urlBuilder = new StringBuilder("https://www.googleapis.com/youtube/v3/search");
        urlBuilder.append("?key=").append(youtubeAPIKey);
        urlBuilder.append("&part=snippet");
        urlBuilder.append("&maxResults=5");
        urlBuilder.append("&type=shorts");
        urlBuilder.append("&q=").append(encodeParam(movieNm) + "예고편");

        return fetchData(urlBuilder, null);
    }

    // URL 인코딩 예외처리
    private String encodeParam(String param) {
        try {
            return URLEncoder.encode(param, "UTF-8");
        } catch (Exception e) {
            log.error("URL 인코딩 실패: {}", param, e);
            return "";
        }
    }

    // IMDB 코드값으로 영화 평점 조회
//    public String getMovieScoreData(String IMDBMovieCd) {
//        StringBuilder urlBuilder = new StringBuilder("https://movies-ratings2.p.rapidapi.com/ratings");
//        urlBuilder.append("?id=").append(encodeParam(IMDBMovieCd));
//
//        Map<String, String> headers = new HashMap<>();
//        headers.put("x-rapidapi-host", "movies-ratings2.p.rapidapi.com");
//        headers.put("x-rapidapi-key", RAPIDAPIKey);
//
//        return fetchData(urlBuilder, headers);
//    }

}
