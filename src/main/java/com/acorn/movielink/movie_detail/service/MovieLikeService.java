package com.acorn.movielink.movie_detail.service;

public interface MovieLikeService {
    void likeMovie(Integer movieId, Integer memId);

    void unlikeMovie(Integer movieId, Integer memId);

    int getLikeCount(Integer movieId);

    boolean isMovieLikedByUser(Integer movieId, Integer memId);
}
