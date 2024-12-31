package com.acorn.movielink.movie_detail.service;

import com.acorn.movielink.movie_detail.repository.MovieLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieLikeServiceImpl implements MovieLikeService {

    @Autowired
    private MovieLikeMapper movieLikeMapper;

    @Transactional
    @Override
    public void likeMovie(Integer movieId, Integer memId) {
        movieLikeMapper.insertLike(movieId, memId);
    }

    @Transactional
    @Override
    public void unlikeMovie(Integer movieId, Integer memId) {
        movieLikeMapper.deleteLike(movieId, memId);
    }

    @Override
    public int getLikeCount(Integer movieId) {
        return movieLikeMapper.getLikeCount(movieId);
    }

    @Override
    public boolean isMovieLikedByUser(Integer movieId, Integer memId) {
        return movieLikeMapper.isMovieLiked(movieId, memId) > 0;
    }
}
