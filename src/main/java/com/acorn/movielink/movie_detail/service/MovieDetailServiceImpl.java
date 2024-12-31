package com.acorn.movielink.movie_detail.service;

import com.acorn.movielink.data.dto.MovieDTO;
import com.acorn.movielink.data.service.APIExplorer;
import com.acorn.movielink.data.repository.MovieRepository;
import com.acorn.movielink.people_detail.dto.People;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MovieDetailServiceImpl implements MovieDetailService {

    @Autowired
    private APIExplorer explorer;

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public MovieDTO getMovieById(Integer movieId) {
        try {
            MovieDTO movie = movieRepository.selectMovieById(movieId); // DB에서 영화 조회
            if (movie == null) { // DB에 없으면 API 호출
                movie = fetchMovieDetails(movieId);
                saveMovieToDB(movie);
            }
            return movie;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("영화 데이터를 가져오는 중 오류가 발생했습니다. movieId: " + movieId, e);
        }
    }

    @Override
    public List<Map<String, Object>> getPeopleById(Integer movieId, String peopleType) {
        try {
            return movieRepository.selectPeopleById(movieId, peopleType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("사람 데이터를 가져오는 중 오류가 발생했습니다. movieId: " + movieId + ", peopleType: " + peopleType, e);
        }
    }

    private void saveMovieToDB(MovieDTO movieDTO) {
        movieRepository.insertMovie(movieDTO); // 영화 정보를 DB에 저장
    }

    private MovieDTO fetchMovieDetails(Integer movieId) throws ParseException {
        String responseKOBIS = explorer.getMovieDataFromKOBIS(String.valueOf(movieId));
        return parseKOBISData(responseKOBIS); // API 데이터를 MovieDTO로 변환
    }

    private MovieDTO parseKOBISData(String responseKOBIS) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(responseKOBIS);
        JSONObject movieInfo = (JSONObject) ((JSONObject) jsonObject.get("movieInfoResult")).get("movieInfo");

        MovieDTO dto = new MovieDTO();
        dto.setMovieNm((String) movieInfo.get("movieNm"));
        dto.setMovieNmEn((String) movieInfo.get("movieNmEn"));
        dto.setMovieOpenDt((String) movieInfo.get("movieOpenDt"));
        dto.setMovieOpenDt((String) movieInfo.get("movieAgeRating"));
        dto.setMovieOpenDt((String) movieInfo.get("movieRunningTime"));
        dto.setMovieNation((String) movieInfo.get("movieNa`tion"));
        dto.setMovieOpenDt((String) movieInfo.get("genre"));
        dto.setMovieRunningTime((String) movieInfo.get("moviePlot"));
        dto.setMovieRunningTime((String) movieInfo.get("movieTrailerUrl"));
        dto.setMovieRunningTime((String) movieInfo.get("moviePosterUrl"));
        dto.setMovieRunningTime((String) movieInfo.get("movieImdbScore"));
        dto.setMovieRunningTime((String) movieInfo.get("movieTMDBScore"));

        List<People> peopleList = new ArrayList<>();

        // 감독 정보 추가
        peopleList.addAll(parsePeopleList((JSONArray) movieInfo.get("directors"), "director"));

        // 배우 정보 추가
        peopleList.addAll(parsePeopleList((JSONArray) movieInfo.get("actors"), "actor"));

        // 스텝 정보 추가
        peopleList.addAll(parsePeopleList((JSONArray) movieInfo.get("staffs"), "staff"));

        dto.setPeopleList(peopleList);
        return dto;
    }

    private List<People> parsePeopleList(JSONArray peopleArray, String type) {
        List<People> peopleList = new ArrayList<>();
        if (peopleArray != null) {
            for (Object obj : peopleArray) {
                JSONObject personObj = (JSONObject) obj;
                People person = new People();
                person.setPeopleNm((String) personObj.get("peopleNm"));
                person.setPeopleNmEn((String) personObj.get("peopleNmEn"));
                person.setPeopleRoleNm((String) personObj.get("peopleRoleNm"));
                person.setPeopleProfileUrl((String) personObj.get("peopleProfileUrl"));
                person.setPeopleType(type);
                peopleList.add(person);
            }
        }
        return peopleList;
    }


}