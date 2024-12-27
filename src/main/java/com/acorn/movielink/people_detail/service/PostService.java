package com.acorn.movielink.people_detail.service;

import com.acorn.movielink.people_detail.dto.Post;
import com.acorn.movielink.people_detail.repository.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    public List<Post> getPostsByTagName(String tagName) {
        return postMapper.selectPostsByTagName(tagName);
    }
}
