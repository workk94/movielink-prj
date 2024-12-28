package com.acorn.movielink.data.repository;

import com.acorn.movielink.data.dto.MovieDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class MovieRepository {

    private final SqlSession sqlSession;

    private static final String namespace = "com.acorn.movielink.MovieMapper.";

    @Autowired
    public MovieRepository(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 영화 전체 조회
    public List<MovieDTO> selectAllMovie() {
        return sqlSession.selectList(namespace + "selectAllMovie");
    }

    // 영화 테이블에 등록된 영화 이름, 개봉일 조회
    public List<Map<String, String>> selectExistingMovies() {
        return sqlSession.selectList(namespace + "selectExistingMovies");
    }

    // 영화 삽입
    public int insertMovie(MovieDTO movie) {
        System.out.println("영화 아이디 : " + movie.getMovieId());
        return sqlSession.insert(namespace + "insertMovie", movie);
    }

    // 장르 id 이름으로 찾기
    public Integer findGenreIdByName(String genreName) {

        // 들어오는 값 ex. 멜로/로멘스, 공포(호러)
        ArrayList<String> list = new ArrayList<>();
        String[] arr = null;

        if(genreName.contains("/")){
            arr = genreName.split("/");

            // Arrays.asList()는 java.util.Arrays.ArrayList 타입을 반환
            // java.util.ArrayList로 변환 안됨
            list = new ArrayList<>(Arrays.asList(arr));
        } else if(genreName.contains("(") && genreName.contains(")")) {
            int start = genreName.indexOf("(");
            int end = genreName.indexOf(")");

            String outside = genreName.substring(0, start).trim(); // 괄호 바깥 : 공포
            String inside = genreName.substring(start + 1, end).trim(); // 괄호 안 : (호러)

            list.add(outside);
            list.add(inside);
        } else {
            list.add(genreName.trim());
        }
        System.out.println(list);

        return sqlSession.selectOne(namespace + "findGenreIdByName", list);
    }

}