package com.acorn.movielink.data.service;

import com.acorn.movielink.data.repository.MovieRepository;
import com.acorn.movielink.data.repository.RawDataRepository;
import com.acorn.movielink.data.dto.MovieDTO;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MovieServiceImpl implements MovieService{

    private final APIExplorer explorer;
    private final MovieRepository movieRepository;
    private final RawDataRepository dataRepository;
    private final PeopleService service;
    @Autowired
    public MovieServiceImpl(APIExplorer explorer,
                            MovieRepository movieRepository,
                            RawDataRepository dataRepository,
                            PeopleService service) {
        this.explorer = explorer;
        this.movieRepository = movieRepository;
        this.dataRepository = dataRepository;
        this.service = service;
    }

    @Override
    @Transactional
    public int saveMovie() {
        long startTime = System.currentTimeMillis(); // 시작 시간
        int totalSaved = 0;

        // 영화 테이블에 있는 기존 영화 정보 가져옴
        List<Map<String, String>> existingMovies = movieRepository.selectExistingMovies();

        // 데이터 테이블에서 중복되지 않은 영화 코드와 개봉일 영화 이름 가져옴
        List<Map<String, String>> movieCodes = dataRepository.selectMovieCodes();

        // 기존 영화와 비교하여 새로운 영화만 필터링
        List<Map<String, String>> newMovies = movieCodes.stream()
                .filter(item -> existingMovies.stream()
                        .noneMatch(existing ->
                                existing.get("movieNm").equals(item.get("movieNm"))
                        )
                ).collect(Collectors.toList());

        log.info("추가된 영화 : {}",newMovies.toString());

        if (newMovies.isEmpty()) {
            log.info("추가할 영화가 없습니다.");
            return totalSaved;
        }

        for (Map<String, String> movie : newMovies) {
            try {
                String movieCd = movie.get("movieCd");
                String responseKOBIS = explorer.getMovieDataFromKOBIS(movieCd);
                MovieDTO dto = parseKOBISData(responseKOBIS);

                // TMDB 데이터 추가
                String responseTMDB = explorer.getMovieDataFromTMDB(dto.getMovieNm(), dto.getMovieOpenDt());
                dto = parseTMDBData(dto, responseTMDB);

                // 유튜브 데이터 추가
                String responseYoutube = explorer.getYoutubeData(dto.getMovieNm());
                dto = parseYoutubeData(dto, responseYoutube);

                // IMDB 데이터 추가
                String releaseYear = dto.getMovieOpenDt().substring(0, 4); // YYYY 포맷 추출
                String responseIMDB = explorer.getIMDBData(dto.getMovieNmEn(), releaseYear);
                dto = parseIMDBData(dto, responseIMDB);

                // 영화 데이터 저장
                movieRepository.insertMovie(dto);
                String id = dto.getMovieId().toString();

                // 인물 데이터 저장
                service.savePeople(dto.getMovieNm(), dto.getMovieOpenDt(), id);


                totalSaved++;
                log.info("영화 코드 {}: 데이터 저장 완료.", movieCd);

            } catch (Exception e) {
                e.printStackTrace();
                log.error("영화 코드 {} 처리 중 오류 발생: {}", movie.get("movieCd"), e.getMessage());
            }
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        log.info("저장 작업이 완료되었습니다. 총 {}건 저장, 총 소요 시간: {} ms", totalSaved, elapsedTime);

        return totalSaved;
    }


    @Override
    public List<MovieDTO> getMovieList() {
        return movieRepository.selectAllMovie();
    }

    @Override
    public List<Map<String, String>> getExistingMovie() {
        return dataRepository.selectMovieCodes();
    }

    // KOBIS 영화 정보
    private MovieDTO parseKOBISData(String response) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(response);

        JSONObject movieInfoResult = (JSONObject) jsonObject.get("movieInfoResult");
        JSONObject movieInfo = (JSONObject) movieInfoResult.get("movieInfo");

        MovieDTO dto = new MovieDTO();

        // 영화 이름
        dto.setMovieNm((String) movieInfo.get("movieNm"));
        // 영화 이름 영문
        dto.setMovieNmEn((String) movieInfo.get("movieNmEn"));
        // 영화 개봉일
        dto.setMovieOpenDt((String) movieInfo.get("openDt"));
        // 영화 상영 시간
        dto.setMovieRunningTime((String) movieInfo.get("showTm"));

        // 영화 연령 등급
        JSONArray audits = (JSONArray) movieInfo.get("audits");
        if (audits != null && !audits.isEmpty()) {
            JSONObject audit = (JSONObject) audits.get(0);
            dto.setMovieAgeRating((String) audit.get("watchGradeNm"));
        }

        // 제작 국가
        JSONArray nations = (JSONArray) movieInfo.get("nations");
        if (nations != null && !nations.isEmpty()) {
            JSONObject nation = (JSONObject) nations.get(0);
            dto.setMovieNation((String) nation.get("nationNm"));
        }

        // 영화 장르
        JSONArray genres = (JSONArray) movieInfo.get("genres");
        if (genres != null && !genres.isEmpty()) {
            JSONObject genreObject = (JSONObject) genres.get(0);
            dto.setGenre((String) genreObject.get("genreNm"));
        }
        return dto;
    }

    private MovieDTO parseTMDBData(MovieDTO dto, String response) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(response);

        Long totalResults = (Long) jsonObject.get("total_results");
        if (totalResults == null || totalResults == 0) {
            return dto;
        }

        JSONArray resultArray = (JSONArray) jsonObject.get("results");
        if (resultArray == null || resultArray.isEmpty()) {
            return dto;
        }

        JSONObject resultObject = (JSONObject) resultArray.get(0);

        String overview = (String) resultObject.get("overview");
        String posterPath = (String) resultObject.get("poster_path");
        String fullPosterPath = posterPath != null ? "https://image.tmdb.org/t/p/w500" + posterPath : null;

        Double voteAverage = resultObject.get("vote_average") != null
                ? Double.parseDouble(resultObject.get("vote_average").toString())
                : 0.0;

        // 영화 줄거리
        dto.setMoviePlot(overview);
        // 영화 대표포스터
        dto.setMoviePosterUrl(fullPosterPath);
        // 영화 TMDB점수 
        dto.setMovieTMDBScore(voteAverage);

        return dto;
    }

    // Youtube 영화 예고편
    private MovieDTO parseYoutubeData(MovieDTO dto, String response) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(response);

        JSONArray itemsArray = (JSONArray) jsonObject.get("items");
        if (itemsArray == null || itemsArray.isEmpty()) {
            return dto;
        }

        for (Object item : itemsArray) {
            JSONObject itemObject = (JSONObject) item;

            JSONObject snippet = (JSONObject) itemObject.get("snippet");
            String title = (String) snippet.get("title");

            if (title != null && title.contains(dto.getMovieNm())) {
                JSONObject idObject = (JSONObject) itemObject.get("id");
                String videoId = (String) idObject.get("videoId");

                if (videoId != null && !videoId.isEmpty()) {
                    String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                    // 영화 예고편
                    dto.setMovieTrailerUrl(videoUrl);
                    break;
                }
            }
        }

        return dto;
    }

    // IMDB 점수
    private MovieDTO parseIMDBData(MovieDTO dto, String response) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(response);

        Long numFound = (Long) jsonObject.get("numFound");
        if (numFound <= 0) {
            return dto;
        }

        JSONArray resultArray = (JSONArray) jsonObject.get("results");
        if (resultArray == null || resultArray.isEmpty()) {
            return dto;
        }

        for (Object resultItem : resultArray) {
            JSONObject resultObject = (JSONObject) resultItem;

            String originalTitle = (String) resultObject.get("originalTitle");
            if (originalTitle != null && dto.getMovieNmEn().contains(originalTitle)) {
                Double averageRating = (resultObject.get("averageRating") != null)
                        ? Double.parseDouble(resultObject.get("averageRating").toString()) : 0.0;

                // 영화 IMDB 점수
                dto.setMovieImdbScore(averageRating);
                break;
            }
        }
        return dto;
    }
}
