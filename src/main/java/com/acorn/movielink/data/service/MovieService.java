package com.acorn.movielink.data.service;


import com.acorn.movielink.data.dto.MovieInfoDTO;

import java.util.List;

public interface MovieService {

    List<MovieInfoDTO> getAllMovie();
}
