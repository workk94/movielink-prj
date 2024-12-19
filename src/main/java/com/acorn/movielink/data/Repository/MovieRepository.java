package com.acorn.movielink.data.Repository;

import com.acorn.movielink.data.dto.MovieDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
        System.out.println("movieId : " + movie.getMovieId());
        return sqlSession.insert(namespace + "insertMovie", movie);
    }
}
