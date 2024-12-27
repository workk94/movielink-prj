package com.acorn.movielink.data.service;

import com.acorn.movielink.data.repository.BoxOfficeDataRepository;
import com.acorn.movielink.data.dto.BoxOfficeDataDTO;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BoxOfficeDataServiceImpl implements BoxOfficeDataService {

    // 배치 사이즈 지정
    private static final int BATCH_SIZE = 50;

    private final APIExplorer explorer;
    private final BoxOfficeDataRepository repository;
    private final SqlSessionFactory sqlSessionFactory; // 추가된 필드

    @Autowired
    public BoxOfficeDataServiceImpl(APIExplorer explorer, BoxOfficeDataRepository repository, SqlSessionFactory sqlSessionFactory) {
        this.explorer = explorer;
        this.repository = repository;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    // 중복된 일자 제거해야됨
    @Override
    @Transactional
    public int saveDataByPeriod(String startDate, String endDate) {
        long startTime = System.currentTimeMillis(); // 시작 시간
        int totalSaved = 0;
        int currentBatchSize = 0;

        try (SqlSession batchSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            BoxOfficeDataRepository batchRepository = new BoxOfficeDataRepository(batchSession);

            List<BoxOfficeDataDTO> list = fetchAndParseData(startDate, endDate);

            for (BoxOfficeDataDTO dto : list) {
                try {
                    batchRepository.insert(dto);
                    currentBatchSize++;

                    // 배치 사이즈에 도달하면 flush
                    if (currentBatchSize >= BATCH_SIZE) {
                        batchSession.flushStatements();
                        batchSession.commit();
                        totalSaved += currentBatchSize;
                        currentBatchSize = 0; // 배치 사이즈 초기화
                        log.info("{}건의 데이터를 저장했습니다. 커밋 완료.", totalSaved);
                    }
                } catch (Exception e) {
                    log.error("데이터 처리 중 오류 발생: {}", e.getMessage());
                }
            }

            // 남아 있는 데이터 처리
            if (currentBatchSize > 0) {
                batchSession.flushStatements();
                batchSession.commit();
                totalSaved += currentBatchSize;
                log.info("마지막 {}건의 데이터를 저장했습니다.", currentBatchSize);
            }
        } catch (Exception e) {
            log.error("배치 저장 중 오류 발생: {}", e.getMessage(), e);
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        log.info("배치 저장 작업 완료, 총 소요 시간: {} ms", elapsedTime);

        return totalSaved;
    }

    @Override
    public List<BoxOfficeDataDTO> findDataBetween(String startDate, String endDate) {
        return repository.findDataBetween(startDate, endDate);
    }

    @Override
    public int deleteDataBetween(String startDate, String endDate) {
        return repository.deleteDataBetween(startDate, endDate);
    }

    @Override
    public  List<Map<String, String>> getAllMovieCode() {
        return repository.selectMovieCodes();
    }

    // api 데이터 가져오고 dto 변환
    private List<BoxOfficeDataDTO> fetchAndParseData(String startDate, String endDate) throws Exception {

        List<BoxOfficeDataDTO> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        while (!start.isAfter(end)) {
            String targetDate = start.format(formatter);

            // API 불러오기
            String response = explorer.getDailyBoxOfficeData(targetDate);

            List<BoxOfficeDataDTO> data = parseData(response, targetDate);
            list.addAll(data);

            // 날짜 + 1
            start = start.plusDays(1);
        }

        return list;
    }

    // json을 RWADataDTO로 변환
    private List<BoxOfficeDataDTO> parseData(String response, String targetDate) throws Exception {
        List<BoxOfficeDataDTO> list = new ArrayList<>();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(response);

        JSONObject boxOfficeResult = (JSONObject) jsonObject.get("boxOfficeResult");
        JSONArray dailyBoxOfficeList = (JSONArray) boxOfficeResult.get("dailyBoxOfficeList");

        for (Object item : dailyBoxOfficeList) {
            JSONObject movie = (JSONObject) item;

            BoxOfficeDataDTO dto = new BoxOfficeDataDTO();
            dto.setRnum((String) movie.get("rnum"));
            dto.setRanking((String) movie.get("rank"));
            dto.setRankInten((String) movie.get("rankInten"));
            dto.setRankOldAndNew((String) movie.get("rankOldAndNew"));
            dto.setMovieCd((String) movie.get("movieCd"));
            dto.setMovieNm((String) movie.get("movieNm"));
            dto.setOpenDt((String) movie.get("openDt"));
            dto.setSalesAmt((String) movie.get("salesAmt"));
            dto.setSalesShare((String) movie.get("salesShare"));
            dto.setSalesInten((String) movie.get("salesInten"));
            dto.setSalesChange((String) movie.get("salesChange"));
            dto.setSalesAcc((String) movie.get("salesAcc"));
            dto.setAudiCnt((String) movie.get("audiCnt"));
            dto.setAudiInten((String) movie.get("audiInten"));
            dto.setAudiChange((String) movie.get("audiChange"));
            dto.setAudiAcc((String) movie.get("audiAcc"));
            dto.setScrnCnt((String) movie.get("scrnCnt"));
            dto.setShowCnt((String) movie.get("showCnt"));
            dto.setBoxOfficeDt(targetDate);

            list.add(dto);
        }

        return list;
    }
}
