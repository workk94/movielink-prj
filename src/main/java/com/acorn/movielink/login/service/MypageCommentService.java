package com.acorn.movielink.login.service;

import com.acorn.movielink.login.repository.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MypageCommentService {

    private final CommentMapper commentMapper;

    @Autowired
    public MypageCommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public int countCommentsByMemId(int memId) {
        return commentMapper.countCommentsByMemId(memId);
    }
}