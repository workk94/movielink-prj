package com.acorn.movielink.data.service;

import com.acorn.movielink.data.Repository.RawDataRepository;
import com.acorn.movielink.data.dto.RAWDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RawDataServiceImpl implements RawDataService {

    // 배치 사이즈 지정
    private static final int BATCH_SIZE = 50;

    private final APIExplorer explorer;
    private final RawDataRepository repository;

    @Autowired
    public RawDataServiceImpl(APIExplorer explorer, RawDataRepository repository) {
        this.explorer = explorer;
        this.repository = repository;
    }

    @Override
    public int saveDataByPeriod(String startDate, String endDate) {
        int totalSaved = 0;
        List<RAWDataDTO> batchData = new ArrayList<>();

        try {
         
            List<RAWDataDTO> list = fetchAndParseData(startDate, endDate);

            for (RAWDataDTO dto : list) {
                batchData.add(dto);

                // 배치 사이즈에 도달할때마다 저장
                if (batchData.size() >= BATCH_SIZE) {
                    totalSaved += repository.insertBatch(batchData);
                    batchData.clear();  // 저장후 초기화
                }
            }

            // 나머지 데이터 저장
            if (!batchData.isEmpty()) {
                totalSaved += repository.insertBatch(batchData);
            }

        } catch (Exception e) {
            log.error("박스 데이터를 저장하는 중 오류 발생, 오류: {}",  e.getMessage(), e);
        }

        return totalSaved;
    }

    @Override
    public List<RAWDataDTO> findDataBetween(String startDate, String endDate) {
        return repository.findDataBetween(startDate, endDate);
    }

    @Override
    public int deleteDataBetween(String startDate, String endDate) {
        return repository.deleteDataBetween(startDate, endDate);
    }

    @Override
    public List<String> getAllMovieCode() {
        return repository.selectMovieCode();
    }

    // api 데이터 가져오고 dto 변환
    private List<RAWDataDTO> fetchAndParseData(String startDate, String endDate) throws Exception {

        List<RAWDataDTO> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        while (!start.isAfter(end)) {
            String targetDate = start.format(formatter);

            // API 불러오기
            String response = explorer.getDailyBoxOfficeData(targetDate);

            List<RAWDataDTO> data = parseData(response, targetDate);
            list.addAll(data);

            // 날짜 + 1
            start = start.plusDays(1);
        }

        return list;
    }

    // json을 RWADataDTO로 변환
    private List<RAWDataDTO> parseData(String response, String targetDate) throws Exception {
        List<RAWDataDTO> list = new ArrayList<>();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(response);

        JSONObject boxOfficeResult = (JSONObject) jsonObject.get("boxOfficeResult");
        JSONArray dailyBoxOfficeList = (JSONArray) boxOfficeResult.get("dailyBoxOfficeList");

        for (Object item : dailyBoxOfficeList) {
            JSONObject movie = (JSONObject) item;

            RAWDataDTO dto = new RAWDataDTO();
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
