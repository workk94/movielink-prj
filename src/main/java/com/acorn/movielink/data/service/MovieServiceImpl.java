package com.acorn.movielink.data.service;

import com.acorn.movielink.data.Repository.GenreRepository;
import com.acorn.movielink.data.Repository.MovieRepository;
import com.acorn.movielink.data.Repository.RawDataRepository;
import com.acorn.movielink.data.dto.MovieDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class MovieServiceImpl implements MovieService{

    private final APIExplorer explorer;
    private final MovieRepository movieRepository;
    private final RawDataRepository dataRepository;
    private final GenreRepository genreRepository;
    private final SqlSessionFactory sqlSessionFactory;

    // 배치 사이즈 지정
    private final int BATCH_SIZE = 30;

    @Autowired
    public MovieServiceImpl(APIExplorer explorer,
                            MovieRepository batchRepository,
                            RawDataRepository dataRepository,
                            GenreRepository genreRepository,
                            SqlSessionFactory sqlSessionFactory) {
        this.explorer = explorer;
        this.movieRepository = batchRepository;
        this.dataRepository = dataRepository;
        this.sqlSessionFactory = sqlSessionFactory;
        this.genreRepository = genreRepository;
    }



    @Override
    @Transactional
    public int saveMovie() {
        long startTime = System.currentTimeMillis(); // 시작시간
        List<String> codes = dataRepository.selectMovieCode(); // kobis 영화코드 가져오기
        int totalSaved = 0;
        int currentBatchSize = 0; // 현재 배치 사이즈를 추적

        try (SqlSession batchSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            MovieRepository batchRepository = new MovieRepository(batchSession);

            for (String code : codes) {
                try {
                    // 영화 데이터를 가져와 파싱
                    String responseKOBIS = explorer.getMovieDataFromKOBIS(code);
                    MovieDTO dto = parseKOBISData(responseKOBIS);

                    String responseTMDB = explorer.getMovieDataFromTMDB(dto.getMovieNm(), dto.getMovieOpenDt());
                    dto = parseTMDBData(dto, responseTMDB);

                    String responseYoutube = explorer.getYoutubeData(dto.getMovieNm());
                    dto = parseYoutubeData(dto, responseYoutube);

                    // 영화 개봉년도 YYYY
                    String releaseYear = dto.getMovieOpenDt().substring(0,4);
                    String reponseIMDB = explorer.getIMDBData(dto.getMovieNmEn(), releaseYear);
                    dto = parseIMDBData(dto, reponseIMDB);

                    batchRepository.insertMovie(dto);

                    String genreNm = dto.getGenre();
                    if(genreNm != null) {
                        genreRepository.insert(genreNm, dto.getMovieId());
                    }


                    totalSaved++;
                    currentBatchSize++;

                    // 배치 사이즈가 지정된 크기에 도달하면 flush
                    if (currentBatchSize >= BATCH_SIZE) {
                        batchSession.flushStatements(); // 배치 작업 실행
                        batchSession.commit();          // 트랜잭션 커밋
                        currentBatchSize = 0;           // 배치 사이즈 초기화
                        log.info("{}건의 영화를 저장했습니다. 커밋 완료.", totalSaved);
                    }

                } catch (Exception e) {
                    log.error("영화 코드 {} 처리 중 오류 발생: {}", code, e.getMessage());
                }
            }

            // 남아 있는 데이터 처리 (마지막 배치가 배치 사이즈보다 작을 경우)
            if (currentBatchSize > 0) {
                batchSession.flushStatements();
                batchSession.commit();
                log.info("마지막 {}건의 영화를 저장했습니다.", currentBatchSize);
            }

            log.info("총 {}건의 영화 데이터를 저장했습니다.", totalSaved);

        } catch (Exception e) {
            log.error("배치 저장 중 오류 발생: {}", e.getMessage(), e);
        }

        long endTime = System.currentTimeMillis(); // 종료시간
        long elapsedTime = endTime - startTime; // 총 걸린 시간 계산
        log.info("배치 저장 작업이 완료되었습니다. 총 소요 시간: {} ms", elapsedTime);

        return totalSaved;
    }

    @Override
    public List<MovieDTO> getMovieList() {
        return null;
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

    // IMDB 영화 코드 및 IMDB 점수 반환
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

            // dto의 영어 이름이 originalTitle에 포함되는지 확인
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

//    // KMDB 영화 정보
//    private MovieDTO parseKMDBData(MovieDTO dto, String response) throws Exception {
//        JSONParser parser = new JSONParser();
//        JSONObject jsonObject = (JSONObject) parser.parse(response);
//        JSONArray  dataArray = (JSONArray) jsonObject.get("Data");
//        JSONObject dataObject = (JSONObject) dataArray.get(0);
//
//        JSONArray resultArray = (JSONArray) dataObject.get("Result");
//        for (Object resultItem : resultArray) {
//            JSONObject resultObject = (JSONObject) resultItem;
//            String title = (String) resultObject.get("title");
//            String movieNm = dto.getMovieNm().trim();
//
//            if (title == null || !title.trim().contains(movieNm)) {
//                continue;
//            }
//            // plots 배열에서 plotLang이 한국어인 plotText 추출
//            JSONObject plots = (JSONObject) resultObject.get("plots");
//            String plotText = null;
//            if (plots != null) {
//                JSONArray plotArray = (JSONArray) plots.get("plot");
//                for (Object plotItem : plotArray) {
//                    JSONObject plotObject = (JSONObject) plotItem;
//                    if (plotObject.get("plotLang").equals("한국어")) {
//                        plotText = (String) plotObject.get("plotText");
//                        break;
//                    }
//                }
//            }
//
//            JSONObject vods = (JSONObject) resultObject.get("vods");
//            String vodUrl = null;
//            if (vods != null) {
//                JSONArray vodArray = (JSONArray) vods.get("vod");
//                if (vodArray != null && !vodArray.isEmpty()) {
//                    // 첫번째 예고편 가져오기
//                    JSONObject firstVodObject = (JSONObject) vodArray.get(0);
//                    vodUrl = (String) firstVodObject.get("vodUrl");
//                }
//            }
//
//            String posters = (String) resultObject.get("posters");
//            String postUrl = null;
//            if (posters != null && !posters.isEmpty()) {
//                // |로 구분되어 있음
//                String[] posterArray = posters.split("\\|");
//                if (posterArray.length > 0) {
//                    postUrl = posterArray[0];
//                }
//            }
//
//            dto.setMoviePlot(plotText);
//            dto.setMovieTrailerUrl(vodUrl);
//            dto.setMoviePosterUrl(postUrl);
//            break;
//        }
//        return dto;
//    }
}
