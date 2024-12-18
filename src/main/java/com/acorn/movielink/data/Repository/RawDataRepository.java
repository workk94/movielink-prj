package com.acorn.movielink.data.Repository;

import com.acorn.movielink.data.dto.RAWDataDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RawDataRepository {

    @Autowired
    private SqlSession session;

    private final String namespace = "com.acorn.movielink.RawDataMapper.";

    // 배치 데이터 삽입
    public int insertBatch(List<RAWDataDTO> list) {
        return session.insert(namespace + "insertBatch", list);
    }

    // 데이터 조회
    public List<RAWDataDTO> findDataBetween(String startDate, String endDate) {
        Map<String, String> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        return session.selectList(namespace + "findDataBetween", params);
    }

    // 데이터 삭제
    public int deleteDataBetween(String startDate, String endDate) {
        Map<String, String> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        return session.delete(namespace + "deleteDataBetween", params);
    }

    // 중복 제외하고 영화코드 가져오기
    public List<String> selectMovieCode(){
        return session.selectList(namespace + "selectMovieCode");
    }

}
