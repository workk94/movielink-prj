package com.acorn.movielink.community.service;


import com.acorn.movielink.community.dto.LikeDTO;
import com.acorn.movielink.community.repository.CommunityLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunityLikeService {

    @Autowired
    private CommunityLikeMapper likeMapper; // LikeMapper 주입

    @Transactional
    public boolean togglePostLike(int postId, int memId) {
        LikeDTO likeDTO = new LikeDTO("POST", postId, memId);


        // 좋아요 여부 확인
        int likeCount = likeMapper.checkLike(likeDTO);

        if (likeCount > 0) {
            // 좋아요 취소
            likeMapper.deleteLike(likeDTO);
            likeMapper.decrementLikeCount(postId); // 게시글 좋아요 수 감소
            return false; // 좋아요 취소
        } else {
            // 좋아요 추가
            likeMapper.insertLike(likeDTO);
            likeMapper.incrementLikeCount(postId); // 게시글 좋아요 수 증가
            return true; // 좋아요 추가
        }
    }

    @Transactional
    public boolean toggleCommentLike(int commentId, int memId) {
        LikeDTO likeDTO = new LikeDTO("COMMENT", commentId, memId);

        // 좋아요 여부 확인
        int likeCount = likeMapper.checkLike(likeDTO);

        if (likeCount > 0) {
            // 좋아요 취소
            likeMapper.deleteLike(likeDTO);
            return false; // 좋아요 취소
        } else {
            // 좋아요 추가
            likeMapper.insertLike(likeDTO);
            return true; // 좋아요 추가
        }
    }

    public boolean isLikedByUser(int postId, int memId) {
        LikeDTO likeDTO = new LikeDTO("POST", postId, memId);
        return likeMapper.checkLike(likeDTO) > 0;
    }
}