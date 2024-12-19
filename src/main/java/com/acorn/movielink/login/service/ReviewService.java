package com.acorn.movielink.login.service;

import com.acorn.movielink.login.dto.Review;
import com.acorn.movielink.login.repository.MemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    private final MemberMapper memberMapper;

    public ReviewService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    public List<Review> getLatestReviews() {
        logger.debug("최신 리뷰 10개 조회 요청");
        List<Review> reviews = memberMapper.findLatestReviews();
        logger.debug("쿼리 결과: {}", reviews);
        return reviews;
    }
}
