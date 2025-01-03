package com.acorn.movielink.data.repository;

import com.acorn.movielink.data.dto.BoxOfficeDataDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BoxOfficeDataRepository {

    private final SqlSession session;

    @Autowired
    public BoxOfficeDataRepository(SqlSession session) {
        this.session = session;
    }

    // 데이터 삽입
    public int insert(BoxOfficeDataDTO dto) {
        return session.insert("com.acorn.movielink.BoxOfficeDataMapper.insert", dto);
    }

    // 데이터 조회
    public List<BoxOfficeDataDTO> findDataBetween(String startDate, String endDate) {
        Map<String, String> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        return session.selectList("com.acorn.movielink.BoxOfficeDataMapper.findDataBetween", params);
    }

    // 중복 제외 영화 코드 가져오기
    public List<Map<String, String>> selectMovieCodes() {
        return session.selectList("com.acorn.movielink.BoxOfficeDataMapper.selectMovieCodes");
    }

    // 데이터 삭제
    public int deleteDataBetween(String startDate, String endDate) {
        Map<String, String> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        return session.delete("com.acorn.movielink.BoxOfficeDataMapper.deleteDataBetween", params);
    }

//    // 중복 제외하고 영화 코드 가져오기
//    public List<String> selectMovieCode() {
//        return session.selectList("com.acorn.movielink.BoxOfficeDataMapper.selectMovieCode");
//    }
}
